
package com.avilaos.scheduler;

import com.avilaos.model.Proceso;
import com.avilaos.structures.Cola;

public class FCFS implements PoliticaScheduler {

    @Override
    public Proceso seleccionar(Cola<Proceso> colaListos) {
        return colaListos.desencolar();
    }

    @Override
    public boolean debeDesalojar(
            Proceso actual,
            Cola<Proceso> colaListos,
            int ciclosQuantum) {

        return false;
    }
}
