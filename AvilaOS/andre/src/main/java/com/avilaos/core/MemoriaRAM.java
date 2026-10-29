
package com.avilaos.core;

import com.avilaos.model.PCB;

public class MemoriaRAM {

    private final int capacidadTotal;
    private int memoriaUsada;

    public MemoriaRAM(int capacidadTotal) {
        if (capacidadTotal <= 0) {
            throw new IllegalArgumentException(
                "La capacidad de RAM debe ser positiva");
        }

        this.capacidadTotal = capacidadTotal;
        this.memoriaUsada = 0;
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public int getMemoriaUsada() {
        return memoriaUsada;
    }

    public int getMemoriaLibre() {
        return capacidadTotal - memoriaUsada;
    }

    public boolean hayEspacio(int cantidad) {
        return cantidad > 0 && cantidad <= getMemoriaLibre();
    }

    public boolean asignarMemoria(PCB pcb) {
        if (pcb == null) {
            throw new IllegalArgumentException(
                "El PCB no puede ser nulo");
        }

        int cantidad = pcb.getMemoriaAsignada();

        if (!hayEspacio(cantidad)) {
            return false;
        }

        memoriaUsada += cantidad;
        return true;
    }

    public void liberarMemoria(PCB pcb) {
        if (pcb == null) {
            throw new IllegalArgumentException(
                "El PCB no puede ser nulo");
        }

        int cantidad = pcb.getMemoriaAsignada();

        if (cantidad > memoriaUsada) {
            throw new IllegalStateException(
                "No se puede liberar mas memoria de la usada");
        }

        memoriaUsada -= cantidad;
    }
}

