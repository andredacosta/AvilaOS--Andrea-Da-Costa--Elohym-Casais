
package com.avilaos.core;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.MotivoBloqueo;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.PoliticaScheduler;
import com.avilaos.structures.Cola;
import com.avilaos.sync.BufferSimulado;

public class Computador implements ComponenteReloj {

    private final int id;
    private final CPU cpu;
    private final MemoriaRAM memoria;

    private final Cola<Proceso> colaNuevos;
    private final Cola<Proceso> colaListos;
    private final Cola<Proceso> colaBloqueados;

    private PoliticaScheduler planificador;
    private ControlProductorConsumidor controlBuffer;

    private int ciclosQuantum;
    private int latenciaRed;

    public Computador(int id, int capacidadRAM,
                      PoliticaScheduler planificador) {

        if (id <= 0 || planificador == null) {
            throw new IllegalArgumentException(
                    "Configuracion invalida");
        }

        this.id = id;
        this.cpu = new CPU();
        this.memoria = new MemoriaRAM(capacidadRAM);

        this.colaNuevos = new Cola<>();
        this.colaListos = new Cola<>();
        this.colaBloqueados = new Cola<>();

        this.planificador = planificador;
        this.controlBuffer = null;
        this.ciclosQuantum = 0;
        this.latenciaRed = 3;
    }

    public void configurarControlBuffer(
            ControlProductorConsumidor control) {

        if (control == null) {
            throw new IllegalArgumentException(
                    "Control de buffer invalido");
        }

        this.controlBuffer = control;
    }

    public void configurarLatenciaRed(int ciclos) {

        if (ciclos < 0) {
            throw new IllegalArgumentException(
                    "La latencia no puede ser negativa");
        }

        this.latenciaRed = ciclos;
    }

    public void agregarProceso(Proceso proceso) {

        if (proceso == null) {
            throw new IllegalArgumentException(
                    "Proceso invalido");
        }

        if (proceso.getPcb().getComputadorId() != id) {
            throw new IllegalArgumentException(
                    "El proceso pertenece a otro computador");
        }

        if (proceso.getPcb().getEstado()
                != EstadoProceso.NUEVO) {
            throw new IllegalStateException(
                    "El proceso debe estar NUEVO");
        }

        colaNuevos.encolar(proceso);
    }

    private void admitirProcesos() {

        int cantidad = colaNuevos.getTamanio();

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso = colaNuevos.desencolar();

            if (proceso.getPcb().getDeadline() <= 0) {
                proceso.vencerDeadline();
                continue;
            }

            if (memoria.asignarMemoria(proceso.getPcb())) {

                proceso.getPcb().setEstado(
                        EstadoProceso.LISTO);

                colaListos.encolar(proceso);

            } else {
                colaNuevos.encolar(proceso);
            }
        }
    }

    private void despachar() {

        if (cpu.estaLibre() && !colaListos.estaVacia()) {

            Proceso siguiente =
                    planificador.seleccionar(colaListos);

            cpu.asignarProceso(siguiente);
            ciclosQuantum = 0;
        }
    }

    private void disminuirDeadline(Proceso proceso) {

        if (proceso.estaTerminado()) {
            return;
        }

        proceso.getPcb().disminuirDeadline();

        if (proceso.getPcb().getDeadline() == 0) {
            proceso.vencerDeadline();
        }
    }

    private void actualizarDeadlineCola(
            Cola<Proceso> cola, boolean tieneMemoria) {

        int cantidad = cola.getTamanio();

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso = cola.desencolar();

            disminuirDeadline(proceso);

            if (proceso.estaTerminado()) {

                if (tieneMemoria) {
                    memoria.liberarMemoria(
                            proceso.getPcb());
                }

            } else {
                cola.encolar(proceso);
            }
        }
    }

    private boolean puedeDesbloquearBuffer(
            Proceso proceso) {

        if (controlBuffer == null) {
            return false;
        }

        BufferSimulado buffer =
                controlBuffer.getBuffer();

        MotivoBloqueo motivo =
                proceso.getPcb().getMotivoBloqueo();

        if (motivo == MotivoBloqueo.BUFFER_LLENO) {
            return !buffer.estaLleno();
        }

        if (motivo == MotivoBloqueo.BUFFER_VACIO) {
            return !buffer.estaVacio();
        }

        return false;
    }

    private void avanzarBloqueados() {

        int cantidad = colaBloqueados.getTamanio();

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso = colaBloqueados.desencolar();

            if (proceso.estaTerminado()) {
                memoria.liberarMemoria(
                        proceso.getPcb());
                continue;
            }

            MotivoBloqueo motivo =
                    proceso.getPcb().getMotivoBloqueo();

            boolean desbloquear = false;

            if (motivo == MotivoBloqueo.ENTRADA_SALIDA) {

                proceso.avanzarIO();
                desbloquear = proceso.ioFinalizada();

            } else if (motivo == MotivoBloqueo.LATENCIA_RED) {

                proceso.getPcb().avanzarLatencia();
                desbloquear =
                        proceso.getPcb().latenciaFinalizada();

            } else if (motivo == MotivoBloqueo.BUFFER_LLENO
                    || motivo == MotivoBloqueo.BUFFER_VACIO) {

                desbloquear =
                        puedeDesbloquearBuffer(proceso);
            }

            if (desbloquear) {

                proceso.getPcb().desbloquear();
                colaListos.encolar(proceso);

            } else {
                colaBloqueados.encolar(proceso);
            }
        }
    }

    private boolean accesoRemoto(Proceso proceso) {

        if (controlBuffer == null) {
            return false;
        }

        int anfitrion = controlBuffer.getBuffer()
                .getComputadorAnfitrionId();

        return proceso.getPcb().getComputadorId()
                != anfitrion;
    }

    private void prepararOperacionBuffer(
            Proceso proceso) {

        if (controlBuffer == null) {
            throw new IllegalStateException(
                    "Buffer no configurado");
        }

        // La latencia se aplica una vez por operacion.
        if (accesoRemoto(proceso)
                && latenciaRed > 0
                && !proceso.getPcb()
                        .isLatenciaAplicadaOperacion()) {

            proceso.getPcb().iniciarLatencia(
                    latenciaRed);

            return;
        }

        // Intentar producir o consumir.
        boolean exito =
                controlBuffer.ejecutarOperacion(proceso);

        if (exito) {

            // La CPU ejecutara la instruccion
            // correspondiente una sola vez.
            proceso.getPcb().completarOperacionBuffer();

        } else {

            TipoProceso tipo =
                    proceso.getPcb().getTipo();

            MotivoBloqueo motivo =
                    tipo == TipoProceso.PRODUCTOR
                    ? MotivoBloqueo.BUFFER_LLENO
                    : MotivoBloqueo.BUFFER_VACIO;

            proceso.getPcb().bloquear(motivo);
        }
    }

    public void ejecutarCiclo() {

        // 1. Actualizar procesos bloqueados.
        avanzarBloqueados();

        // 2. Admitir nuevos procesos.
        admitirProcesos();

        // 3. Evaluar desalojo del proceso actual.
        Proceso actual = cpu.getProcesoActual();

        if (actual != null
                && planificador.debeDesalojar(
                        actual, colaListos, ciclosQuantum)) {

            Proceso desalojado =
                    cpu.desalojarProceso();

            colaListos.encolar(desalojado);
            ciclosQuantum = 0;
        }

        // 4. Despachar un proceso.
        despachar();

        actual = cpu.getProcesoActual();

        // 5. Preparar acceso al buffer.
        if (actual != null
                && actual.esProductorConsumidor()) {

            prepararOperacionBuffer(actual);
        }

        // 6. Ejecutar ciclo de CPU.
        // Un proceso bloqueado no avanza instrucciones.
        cpu.ejecutarCiclo();

        // 7. Registrar el resultado.
        if (actual != null) {

            if (actual.estaTerminado()) {

                memoria.liberarMemoria(
                        actual.getPcb());

                ciclosQuantum = 0;

            } else if (actual.getPcb().getEstado()
                    == EstadoProceso.BLOQUEADO) {

                cpu.liberarCPU();
                colaBloqueados.encolar(actual);
                ciclosQuantum = 0;

            } else {

                ciclosQuantum++;
            }
        }

        // 8. Actualizar deadlines de las colas.
        actualizarDeadlineCola(colaNuevos, false);
        actualizarDeadlineCola(colaListos, true);
        actualizarDeadlineCola(colaBloqueados, true);

        // 9. Actualizar deadline de la CPU.
        Proceso ejecutando = cpu.getProcesoActual();

        if (ejecutando != null) {

            disminuirDeadline(ejecutando);

            if (ejecutando.vencioPorDeadline()) {

                cpu.liberarCPU();

                memoria.liberarMemoria(
                        ejecutando.getPcb());

                ciclosQuantum = 0;
            }
        }
    }

    public void cambiarPlanificador(
            PoliticaScheduler nuevoPlanificador) {

        if (nuevoPlanificador == null) {
            throw new IllegalArgumentException(
                    "Planificador invalido");
        }

        this.planificador = nuevoPlanificador;
        ciclosQuantum = 0;
    }

    public int getId() {
        return id;
    }

    public CPU getCpu() {
        return cpu;
    }

    public MemoriaRAM getMemoria() {
        return memoria;
    }

    public int getLatenciaRed() {
        return latenciaRed;
    }

    public int getProcesosNuevos() {
        return colaNuevos.getTamanio();
    }

    public int getProcesosListos() {
        return colaListos.getTamanio();
    }

    public int getProcesosBloqueados() {
        return colaBloqueados.getTamanio();
    }
}
