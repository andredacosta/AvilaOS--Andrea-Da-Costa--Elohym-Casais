package com.avilaos.core;

import com.avilaos.model.PCB;
import java.util.concurrent.Semaphore;

/** Contabilidad de RAM por computador; no realiza paginacion ni memoria virtual. */
public class MemoriaRAM {
    private final int capacidadTotal;
    private int memoriaUsada;
    private Asignacion asignaciones;
    private final Semaphore mutex = new Semaphore(1);

    private static final class Asignacion {
        final int procesoId;
        final int cantidad;
        Asignacion siguiente;

        Asignacion(int procesoId, int cantidad, Asignacion siguiente) {
            this.procesoId = procesoId;
            this.cantidad = cantidad;
            this.siguiente = siguiente;
        }
    }

    public MemoriaRAM(int capacidadTotal) {
        if (capacidadTotal <= 0) {
            throw new IllegalArgumentException("La capacidad de RAM debe ser positiva");
        }
        this.capacidadTotal = capacidadTotal;
    }

    public int getCapacidadTotal() {
        return capacidadTotal;
    }

    public int getMemoriaUsada() {
        mutex.acquireUninterruptibly();
        try { return memoriaUsada; }
        finally { mutex.release(); }
    }

    public int getMemoriaLibre() {
        mutex.acquireUninterruptibly();
        try { return capacidadTotal - memoriaUsada; }
        finally { mutex.release(); }
    }

    public boolean hayEspacio(int cantidad) {
        mutex.acquireUninterruptibly();
        try { return cantidad > 0 && cantidad <= capacidadTotal - memoriaUsada; }
        finally { mutex.release(); }
    }

    private Asignacion buscar(int id) {
        for (Asignacion actual = asignaciones; actual != null; actual = actual.siguiente) {
            if (actual.procesoId == id) { return actual; }
        }
        return null;
    }

    public boolean asignarMemoria(PCB pcb) {
        if (pcb == null) { throw new IllegalArgumentException("PCB nulo"); }
        mutex.acquireUninterruptibly();
        try {
            int cantidad = pcb.getMemoriaAsignada();
            if (buscar(pcb.getId()) != null) { return false; }
            if (cantidad <= 0 || cantidad > capacidadTotal - memoriaUsada) { return false; }
            asignaciones = new Asignacion(pcb.getId(), cantidad, asignaciones);
            memoriaUsada += cantidad;
            return true;
        } finally { mutex.release(); }
    }

    public void liberarMemoria(PCB pcb) {
        if (pcb == null) { throw new IllegalArgumentException("PCB nulo"); }
        mutex.acquireUninterruptibly();
        try {
            Asignacion anterior = null;
            Asignacion actual = asignaciones;
            while (actual != null && actual.procesoId != pcb.getId()) {
                anterior = actual;
                actual = actual.siguiente;
            }
            if (actual == null) {
                throw new IllegalStateException("El proceso no tiene memoria reservada");
            }
            if (anterior == null) { asignaciones = actual.siguiente; }
            else { anterior.siguiente = actual.siguiente; }
            memoriaUsada -= actual.cantidad;
        } finally { mutex.release(); }
    }
}
