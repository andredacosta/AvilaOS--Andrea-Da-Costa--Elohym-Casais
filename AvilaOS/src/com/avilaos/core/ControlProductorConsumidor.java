
package com.avilaos.core;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.FaseOperacionBuffer;
import com.avilaos.model.MotivoBloqueo;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.sync.BufferSimulado;
import com.avilaos.sync.SemaforoSimulado;

public class ControlProductorConsumidor {

    private final BufferSimulado buffer;

    private int producidos;
    private int consumidos;

    public ControlProductorConsumidor(BufferSimulado buffer) {

        if (buffer == null) {
            throw new IllegalArgumentException(
                    "El buffer no puede ser nulo");
        }

        this.buffer = buffer;
        this.producidos = 0;
        this.consumidos = 0;
    }

    // Ejecuta una fase del protocolo productor-consumidor.
    // Una operacion completa requiere varios ciclos.
    public synchronized boolean ejecutarOperacion(
            Proceso proceso) {

        if (proceso == null
                || proceso.getPcb().getEstado()
                != EstadoProceso.EJECUCION) {

            throw new IllegalArgumentException(
                    "Proceso no disponible");
        }

        TipoProceso tipo = proceso.getPcb().getTipo();

        if (tipo != TipoProceso.PRODUCTOR
                && tipo != TipoProceso.CONSUMIDOR) {

            throw new IllegalArgumentException(
                    "El proceso no es productor ni consumidor");
        }

        FaseOperacionBuffer fase =
                proceso.getPcb().getFaseOperacionBuffer();

        switch (fase) {

            // Solicitar espacio vacio o elemento disponible.
            case ESPERANDO_PERMISO -> {

                SemaforoSimulado semaforo =
                        tipo == TipoProceso.PRODUCTOR
                        ? buffer.getVacios()
                        : buffer.getLlenos();

                MotivoBloqueo motivo =
                        tipo == TipoProceso.PRODUCTOR
                        ? MotivoBloqueo.BUFFER_LLENO
                        : MotivoBloqueo.BUFFER_VACIO;

                if (!semaforo.semWait(proceso, motivo)) {
                    return false;
                }

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.ESPERANDO_MUTEX);

                return true;
            }

            // Solicitar acceso exclusivo al buffer.
            case ESPERANDO_MUTEX -> {

                if (!buffer.getMutex().semWait(
                        proceso,
                        MotivoBloqueo.MUTEX_OCUPADO)) {

                    return false;
                }

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.OPERANDO_BUFFER);

                return true;
            }

            // Insertar o extraer un elemento.
            case OPERANDO_BUFFER -> {

                if (tipo == TipoProceso.PRODUCTOR) {

                    String elemento =
                            "P" + proceso.getPcb().getId()
                            + "-I"
                            + proceso.getPcb()
                                    .getProgramCounter();

                    boolean exito =
                            buffer.intentarProducir(elemento);

                    if (!exito) {
                        throw new IllegalStateException(
                                "Permiso de espacio inconsistente");
                    }

                    producidos++;

                } else {

                    BufferSimulado.ResultadoConsumo resultado =
                            buffer.intentarConsumir();

                    if (!resultado.isExitoso()) {
                        throw new IllegalStateException(
                                "Permiso de elemento inconsistente");
                    }

                    consumidos++;
                }

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.LIBERANDO_MUTEX);

                return true;
            }

            // Liberar la region critica.
            case LIBERANDO_MUTEX -> {

                buffer.getMutex().semSignal();

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.NOTIFICANDO_BUFFER);

                return true;
            }

            // Notificar elementos o espacios disponibles.
            case NOTIFICANDO_BUFFER -> {

                if (tipo == TipoProceso.PRODUCTOR) {
                    buffer.getLlenos().semSignal();
                } else {
                    buffer.getVacios().semSignal();
                }

                // Preparar la siguiente operacion.
                proceso.getPcb().completarOperacionBuffer();
                proceso.getPcb().reiniciarFaseOperacionBuffer();

                return true;
            }

            case COMPLETADA -> {

                proceso.getPcb().completarOperacionBuffer();
                proceso.getPcb().reiniciarFaseOperacionBuffer();

                return true;
            }

            default -> throw new IllegalStateException(
                    "Fase de operacion desconocida");
        }
    }

    public synchronized int getProducidos() {
        return producidos;
    }

    public synchronized int getConsumidos() {
        return consumidos;
    }

    public BufferSimulado getBuffer() {
        return buffer;
    }
}
