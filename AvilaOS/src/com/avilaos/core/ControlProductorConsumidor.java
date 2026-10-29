
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

    public synchronized boolean ejecutarOperacion(
            Proceso proceso) {

        if (proceso == null
                || proceso.getPcb().getEstado()
                != EstadoProceso.EJECUCION) {
            throw new IllegalArgumentException(
                    "Proceso no disponible");
        }

        TipoProceso tipo = proceso.getPcb().getTipo();

        validarTipo(tipo);

        FaseOperacionBuffer fase =
                proceso.getPcb().getFaseOperacionBuffer();

        switch (fase) {

            case ESPERANDO_PERMISO -> {

                SemaforoSimulado semaforo =
                        semaforoDeEntrada(tipo);

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

            case ESPERANDO_MUTEX -> {

                if (!buffer.getMutex().semWait(
                        proceso, MotivoBloqueo.MUTEX_OCUPADO)) {
                    return false;
                }

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.OPERANDO_BUFFER);

                return true;
            }

            case OPERANDO_BUFFER -> {

                if (tipo == TipoProceso.PRODUCTOR) {

                    String elemento =
                            "P" + proceso.getPcb().getId()
                            + "-I"
                            + proceso.getPcb()
                                    .getProgramCounter();

                    if (!buffer.intentarProducir(elemento)) {
                        throw new IllegalStateException(
                                "Inconsistencia al producir");
                    }

                    producidos++;

                } else {

                    BufferSimulado.ResultadoConsumo resultado =
                            buffer.intentarConsumir();

                    if (!resultado.isExitoso()) {
                        throw new IllegalStateException(
                                "Inconsistencia al consumir");
                    }

                    consumidos++;
                }

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.LIBERANDO_MUTEX);

                return true;
            }

            case LIBERANDO_MUTEX -> {

                buffer.getMutex().semSignal();

                proceso.getPcb().setFaseOperacionBuffer(
                        FaseOperacionBuffer.NOTIFICANDO_BUFFER);

                return true;
            }

            case NOTIFICANDO_BUFFER -> {

                semaforoDeSalida(tipo).semSignal();

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
                    "Fase desconocida");
        }
    }

    public synchronized void cancelarOperacion(
            Proceso proceso) {

        if (proceso == null) {
            return;
        }

        TipoProceso tipo = proceso.getPcb().getTipo();

        if (tipo != TipoProceso.PRODUCTOR
                && tipo != TipoProceso.CONSUMIDOR) {
            return;
        }

        FaseOperacionBuffer fase =
                proceso.getPcb().getFaseOperacionBuffer();

        SemaforoSimulado entrada =
                semaforoDeEntrada(tipo);

        SemaforoSimulado salida =
                semaforoDeSalida(tipo);

        // Retirar al proceso de todas las esperas.
        // Un permiso transferido se reasigna o recupera.
        entrada.cancelarEspera(proceso);
        buffer.getMutex().cancelarEspera(proceso);

        switch (fase) {

            case ESPERANDO_PERMISO -> {
                // No posee permisos adquiridos.
            }

            case ESPERANDO_MUTEX -> {
                // Habia adquirido el permiso de entrada.
                entrada.semSignal();
            }

            case OPERANDO_BUFFER -> {
                // Tenia el permiso de entrada y el mutex.
                buffer.getMutex().semSignal();
                entrada.semSignal();
            }

            case LIBERANDO_MUTEX -> {
                // Ya modifico el buffer:
                // liberar mutex y notificar el resultado.
                buffer.getMutex().semSignal();
                salida.semSignal();
            }

            case NOTIFICANDO_BUFFER -> {
                // El mutex ya fue liberado.
                // Falta notificar la modificacion.
                salida.semSignal();
            }

            case COMPLETADA -> {
                // No hay recursos pendientes.
            }
        }

        proceso.getPcb().completarOperacionBuffer();
        proceso.getPcb().reiniciarFaseOperacionBuffer();
    }

    private void validarTipo(TipoProceso tipo) {

        if (tipo != TipoProceso.PRODUCTOR
                && tipo != TipoProceso.CONSUMIDOR) {
            throw new IllegalArgumentException(
                    "Tipo de proceso invalido");
        }
    }

    private SemaforoSimulado semaforoDeEntrada(
            TipoProceso tipo) {

        return tipo == TipoProceso.PRODUCTOR
                ? buffer.getVacios()
                : buffer.getLlenos();
    }

    private SemaforoSimulado semaforoDeSalida(
            TipoProceso tipo) {

        return tipo == TipoProceso.PRODUCTOR
                ? buffer.getLlenos()
                : buffer.getVacios();
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
