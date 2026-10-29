
package com.avilaos.model;

public class PCB {

    private final int id;
    private final String nombre;
    private final int computadorId;
    private final TipoProceso tipo;

    private EstadoProceso estado;

    private int prioridad;
    private int memoriaAsignada;
    private int deadline;
    private int tiempoRestante;

    private int programCounter;
    private int mar;

    public PCB(int id, String nombre, int computadorId,
               TipoProceso tipo, int prioridad,
               int memoriaRequerida, int deadline,
               int totalInstrucciones) {

        if (id <= 0 || computadorId <= 0) {
            throw new IllegalArgumentException("ID invalido");
        }

        if (nombre == null || nombre.isBlank() || tipo == null) {
            throw new IllegalArgumentException("Proceso invalido");
        }

        if (memoriaRequerida <= 0 || deadline <= 0
                || totalInstrucciones <= 0) {
            throw new IllegalArgumentException(
                "Los valores deben ser positivos");
        }

        this.id = id;
        this.nombre = nombre;
        this.computadorId = computadorId;
        this.tipo = tipo;

        this.estado = EstadoProceso.NUEVO;
        this.prioridad = prioridad;
        this.memoriaAsignada = memoriaRequerida;
        this.deadline = deadline;
        this.tiempoRestante = totalInstrucciones;

        this.programCounter = 0;
        this.mar = 0;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getComputadorId() {
        return computadorId;
    }

    public TipoProceso getTipo() {
        return tipo;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public void setEstado(EstadoProceso estado) {
        if (estado == null) {
            throw new IllegalArgumentException("Estado invalido");
        }
        this.estado = estado;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getMemoriaAsignada() {
        return memoriaAsignada;
    }

    public int getDeadline() {
        return deadline;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public int getMar() {
        return mar;
    }

    public void avanzarInstruccion() {
        if (tiempoRestante > 0
                && estado == EstadoProceso.EJECUCION) {
            programCounter++;
            mar++;
            tiempoRestante--;
        }
    }

    public void disminuirDeadline() {
        if (deadline > 0
                && estado != EstadoProceso.TERMINADO) {
            deadline--;
        }
    }
}

