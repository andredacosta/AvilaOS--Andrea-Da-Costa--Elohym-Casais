
package com.avilaos.scheduler;

import com.avilaos.model.Proceso;
import com.avilaos.structures.Cola;

public class RoundRobin implements PoliticaScheduler {

    private final int quantum;

    public RoundRobin(int quantum) {
        if (quantum <= 0) {
            throw new IllegalArgumentException(
                "El quantum debe ser positivo");
        }
        this.quantum = quantum;
    }

    @Override
    public Proceso seleccionar(Cola<Proceso> colaListos) {
        return colaListos.desencolar();
    }

    @Override
    public boolean debeDesalojar(
            Proceso actual,
            Cola<Proceso> colaListos,
            int ciclosQuantum) {

        return actual != null
                && !colaListos.estaVacia()
                && ciclosQuantum >= quantum;
    }

    public int getQuantum() {
        return quantum;
    }
}
