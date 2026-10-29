
package com.avilaos.model;

public class Proceso {

    private final PCB pcb;

    public Proceso(PCB pcb) {
        if (pcb == null) {
            throw new IllegalArgumentException(
                    "El PCB no puede ser nulo");
        }
        this.pcb = pcb;
    }

    public PCB getPcb() {
        return pcb;
    }

    public boolean estaTerminado() {
        return pcb.getEstado() == EstadoProceso.TERMINADO;
    }

    public boolean puedeEjecutarse() {
        return pcb.getEstado() == EstadoProceso.EJECUCION
                && pcb.getTiempoRestante() > 0
                && pcb.getDeadline() > 0;
    }

    public boolean esProductorConsumidor() {
        return pcb.getTipo() == TipoProceso.PRODUCTOR
                || pcb.getTipo() == TipoProceso.CONSUMIDOR;
    }

    // Ejecuta una instruccion que ya fue autorizada.
    public void ejecutarCiclo() {

        if (!puedeEjecutarse()) {
            return;
        }

        pcb.avanzarInstruccion();

        if (pcb.getTiempoRestante() == 0) {
            finalizar();
            return;
        }

        if (pcb.debeSolicitarIO()) {
            pcb.iniciarIO();
        }
    }

    // Se utilizara cuando la operacion del buffer
    // se complete satisfactoriamente.
    public void completarOperacionBuffer() {

        if (!esProductorConsumidor()) {
            throw new IllegalStateException(
                    "El proceso no utiliza el buffer");
        }

        if (!puedeEjecutarse()) {
            throw new IllegalStateException(
                    "El proceso no puede ejecutar");
        }

        pcb.avanzarInstruccion();

        if (pcb.getTiempoRestante() == 0) {
            finalizar();
        }
    }

    public void avanzarIO() {
        pcb.avanzarIO();
    }

    public boolean ioFinalizada() {
        return pcb.ioFinalizada();
    }

    public void finalizar() {
        if (!estaTerminado()) {
            pcb.setEstado(EstadoProceso.TERMINADO);
        }
    }

    public void vencerDeadline() {
        if (!estaTerminado()) {
            pcb.vencerDeadline();
        }
    }

    public boolean vencioPorDeadline() {
        return pcb.isVencidoPorDeadline();
    }
}
