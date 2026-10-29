
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

    public void ejecutarCiclo() {
        if (!puedeEjecutarse()) {
            return;
        }

        pcb.avanzarInstruccion();

        if (pcb.getTiempoRestante() == 0) {
            pcb.setEstado(EstadoProceso.TERMINADO);
        }
    }

    public void finalizar() {
        pcb.setEstado(EstadoProceso.TERMINADO);
    }
}

