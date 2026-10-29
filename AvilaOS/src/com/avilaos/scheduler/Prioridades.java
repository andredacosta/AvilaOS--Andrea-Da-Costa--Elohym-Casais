
package com.avilaos.scheduler;

import com.avilaos.model.Proceso;
import com.avilaos.structures.Cola;

public class Prioridades implements PoliticaScheduler {

    @Override
    public Proceso seleccionar(Cola<Proceso> colaListos) {

        if (colaListos.estaVacia()) {
            return null;
        }

        int cantidad = colaListos.getTamanio();
        Proceso elegido = null;

        for (int i = 0; i < cantidad; i++) {
            Proceso proceso = colaListos.desencolar();

            if (elegido == null
                    || proceso.getPcb().getPrioridad()
                    < elegido.getPcb().getPrioridad()) {

                if (elegido != null) {
                    colaListos.encolar(elegido);
                }

                elegido = proceso;
            } else {
                colaListos.encolar(proceso);
            }
        }

        return elegido;
    }

    @Override
    public boolean debeDesalojar(
            Proceso actual,
            Cola<Proceso> colaListos,
            int ciclosQuantum) {

        if (actual == null || colaListos.estaVacia()) {
            return false;
        }

        int prioridadActual = actual.getPcb().getPrioridad();
        int cantidad = colaListos.getTamanio();
        boolean desalojar = false;

        for (int i = 0; i < cantidad; i++) {
            Proceso proceso = colaListos.desencolar();

            if (proceso.getPcb().getPrioridad()
                    < prioridadActual) {
                desalojar = true;
            }

            colaListos.encolar(proceso);
        }

        return desalojar;
    }
}
