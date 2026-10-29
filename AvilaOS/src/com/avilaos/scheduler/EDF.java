
package com.avilaos.scheduler;

import com.avilaos.model.Proceso;
import com.avilaos.structures.Cola;

public class EDF implements PoliticaScheduler {

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
                    || proceso.getPcb().getDeadline()
                    < elegido.getPcb().getDeadline()) {

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

        int deadlineActual = actual.getPcb().getDeadline();
        int cantidad = colaListos.getTamanio();
        boolean desalojar = false;

        for (int i = 0; i < cantidad; i++) {
            Proceso proceso = colaListos.desencolar();

            if (proceso.getPcb().getDeadline()
                    < deadlineActual) {
                desalojar = true;
            }

            colaListos.encolar(proceso);
        }

        return desalojar;
    }
}
