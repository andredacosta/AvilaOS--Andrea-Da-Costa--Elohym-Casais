
package com.avilaos.core;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.FaseOperacionBuffer;
import com.avilaos.model.MotivoBloqueo;
import com.avilaos.model.Proceso;
import com.avilaos.scheduler.PoliticaScheduler;
import com.avilaos.structures.Cola;
import com.avilaos.sync.BufferSimulado;
import com.avilaos.sync.SemaforoSimulado;

public class Computador implements ComponenteReloj {

    private final int id;
    private final CPU cpu;
    private final MemoriaRAM memoria;

    private final Cola<Proceso> colaNuevos;
    private final Cola<Proceso> colaListos;
    private final Cola<Proceso> colaBloqueados;
    private final Cola<Proceso> colaTerminados;

    private PoliticaScheduler planificador;
    private ControlProductorConsumidor controlBuffer;

    private int ciclosQuantum;
    private int latenciaRed;

    public Computador(
            int id,
            int capacidadRAM,
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
        this.colaTerminados = new Cola<>();

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
                    "Latencia invalida");
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
                    "Proceso de otro computador");
        }

        if (proceso.getPcb().getEstado()
                != EstadoProceso.NUEVO) {
            throw new IllegalStateException(
                    "El proceso debe estar NUEVO");
        }

        colaNuevos.encolar(proceso);
    }

    // =========================================
    // ADMISION DE PROCESOS
    // =========================================

    private void admitirProcesos() {

        int cantidad = colaNuevos.getTamanio();

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso = colaNuevos.desencolar();

            if (proceso.estaTerminado()) {
                registrarTerminado(proceso, false);
                continue;
            }

            if (memoria.asignarMemoria(
                    proceso.getPcb())) {

                proceso.getPcb().setEstado(
                        EstadoProceso.LISTO);

                colaListos.encolar(proceso);

            } else {

                colaNuevos.encolar(proceso);
            }
        }
    }

    // =========================================
    // DESPACHO Y PLANIFICACION
    // =========================================

    private void despachar() {

        if (!cpu.estaLibre()
                || colaListos.estaVacia()) {
            return;
        }

        Proceso siguiente =
                planificador.seleccionar(colaListos);

        if (siguiente != null) {
            cpu.asignarProceso(siguiente);
            ciclosQuantum = 0;
        }
    }

    private void evaluarDesalojo() {

        Proceso actual = cpu.getProcesoActual();

        if (actual == null) {
            return;
        }

        if (planificador.debeDesalojar(
                actual,
                colaListos,
                ciclosQuantum)) {

            Proceso desalojado =
                    cpu.desalojarProceso();

            if (desalojado != null) {
                colaListos.encolar(desalojado);
            }

            ciclosQuantum = 0;
        }
    }

    // =========================================
    // CANCELACION SEGURA
    // =========================================

    private void registrarTerminado(
            Proceso proceso,
            boolean teniaMemoria) {

        if (proceso == null) {
            return;
        }

        if (controlBuffer != null
                && proceso.esProductorConsumidor()) {

            controlBuffer.cancelarOperacion(proceso);
        }

        if (teniaMemoria) {
            memoria.liberarMemoria(
                    proceso.getPcb());
        }

        colaTerminados.encolar(proceso);
    }

    private void vencerProceso(Proceso proceso) {

        if (proceso == null
                || proceso.estaTerminado()) {
            return;
        }

        proceso.vencerDeadline();
    }

    private void disminuirDeadline(Proceso proceso) {

        if (proceso == null
                || proceso.estaTerminado()) {
            return;
        }

        proceso.getPcb().disminuirDeadline();

        if (proceso.getPcb().getDeadline() <= 0) {
            vencerProceso(proceso);
        }
    }

    private void actualizarDeadlineCola(
            Cola<Proceso> cola,
            boolean tieneMemoria) {

        int cantidad = cola.getTamanio();

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso = cola.desencolar();

            disminuirDeadline(proceso);

            if (proceso.estaTerminado()) {

                registrarTerminado(
                        proceso,
                        tieneMemoria);

            } else {

                cola.encolar(proceso);
            }
        }
    }

    // =========================================
    // DESPERTAR POR SEMAFORO
    // =========================================

    private boolean tienePermisoParaDespertar(
            Proceso proceso) {

        if (controlBuffer == null) {
            return false;
        }

        BufferSimulado buffer =
                controlBuffer.getBuffer();

        MotivoBloqueo motivo =
                proceso.getPcb().getMotivoBloqueo();

        SemaforoSimulado semaforo = null;

        if (motivo == MotivoBloqueo.BUFFER_LLENO) {

            semaforo = buffer.getVacios();

        } else if (motivo
                == MotivoBloqueo.BUFFER_VACIO) {

            semaforo = buffer.getLlenos();

        } else if (motivo
                == MotivoBloqueo.MUTEX_OCUPADO) {

            semaforo = buffer.getMutex();
        }

        return semaforo != null
                && semaforo.tienePermisoTransferido(
                        proceso);
    }

    // =========================================
    // PROCESOS BLOQUEADOS
    // =========================================

    private void avanzarBloqueados() {

        int cantidad = colaBloqueados.getTamanio();

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso =
                    colaBloqueados.desencolar();

            if (proceso.estaTerminado()) {

                registrarTerminado(proceso, true);
                continue;
            }

            MotivoBloqueo motivo =
                    proceso.getPcb().getMotivoBloqueo();

            boolean despertar = false;

            if (motivo
                    == MotivoBloqueo.ENTRADA_SALIDA) {

                proceso.avanzarIO();
                despertar = proceso.ioFinalizada();

            } else if (motivo
                    == MotivoBloqueo.LATENCIA_RED) {

                proceso.getPcb().avanzarLatencia();

                despertar = proceso.getPcb()
                        .latenciaFinalizada();

            } else if (motivo
                    == MotivoBloqueo.BUFFER_LLENO
                    || motivo
                    == MotivoBloqueo.BUFFER_VACIO
                    || motivo
                    == MotivoBloqueo.MUTEX_OCUPADO) {

                despertar =
                        tienePermisoParaDespertar(proceso);
            }

            if (despertar) {

                proceso.getPcb().desbloquear();
                colaListos.encolar(proceso);

            } else {

                colaBloqueados.encolar(proceso);
            }
        }
    }

    // =========================================
    // ACCESO LOCAL Y REMOTO
    // =========================================

    private boolean accesoRemoto(
            Proceso proceso) {

        if (controlBuffer == null) {
            return false;
        }

        return proceso.getPcb().getComputadorId()
                != controlBuffer.getBuffer()
                        .getComputadorAnfitrionId();
    }

    private void prepararOperacionBuffer(
            Proceso proceso) {

        if (controlBuffer == null) {
            throw new IllegalStateException(
                    "Buffer no configurado");
        }

        if (accesoRemoto(proceso)
                && latenciaRed > 0
                && !proceso.getPcb()
                        .isLatenciaAplicadaOperacion()) {

            proceso.getPcb().iniciarLatencia(
                    latenciaRed);

            return;
        }

        FaseOperacionBuffer faseAnterior =
                proceso.getPcb()
                        .getFaseOperacionBuffer();

        controlBuffer.ejecutarOperacion(proceso);

        // Una operacion logica completa termina
        // despues del ultimo semSignal.
        if (faseAnterior
                == FaseOperacionBuffer.NOTIFICANDO_BUFFER
                && proceso.getPcb().getEstado()
                == EstadoProceso.EJECUCION) {

            proceso.completarOperacionBuffer();
        }
    }

    // =========================================
    // CICLO DEL COMPUTADOR
    // =========================================

    @Override
    public void ejecutarCiclo() {

        // 1. Actualizar procesos bloqueados.
        avanzarBloqueados();

        // 2. Admitir procesos nuevos.
        admitirProcesos();

        // 3. Evaluar desalojo.
        evaluarDesalojo();

        // 4. Despachar siguiente proceso.
        despachar();

        Proceso actual = cpu.getProcesoActual();

        // 5. Ejecutar CPU.
        if (actual != null
                && actual.esProductorConsumidor()) {

            cpu.ejecutarCicloSistema();

            prepararOperacionBuffer(actual);

        } else {

            cpu.ejecutarCiclo();
        }

        // 6. Procesar resultado de ejecucion.
        if (actual != null) {

            if (actual.estaTerminado()) {

                cpu.liberarCPU();

                registrarTerminado(actual, true);
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

        // 7. Actualizar deadlines de las colas.
        actualizarDeadlineCola(
                colaNuevos, false);

        actualizarDeadlineCola(
                colaListos, true);

        actualizarDeadlineCola(
                colaBloqueados, true);

        // 8. Actualizar deadline del proceso
        // que permanece ejecutando.
        Proceso ejecutando =
                cpu.getProcesoActual();

        if (ejecutando != null) {

            disminuirDeadline(ejecutando);

            if (ejecutando.estaTerminado()) {

                cpu.liberarCPU();

                registrarTerminado(
                        ejecutando, true);

                ciclosQuantum = 0;
            }
        }
    }

    // =========================================
    // CONFIGURACION
    // =========================================

    public void cambiarPlanificador(
            PoliticaScheduler nuevoPlanificador) {

        if (nuevoPlanificador == null) {
            throw new IllegalArgumentException(
                    "Planificador invalido");
        }

        this.planificador = nuevoPlanificador;
        ciclosQuantum = 0;
    }

    // =========================================
    // GETTERS
    // =========================================

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

    public int getProcesosTerminados() {
        return colaTerminados.getTamanio();
    }
}
