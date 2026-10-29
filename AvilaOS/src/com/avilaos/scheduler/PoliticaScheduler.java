
package com.avilaos.scheduler;

import com.avilaos.model.Proceso;
import com.avilaos.structures.Cola;

public interface PoliticaScheduler {

    Proceso seleccionar(Cola<Proceso> colaListos);

    boolean debeDesalojar(
        Proceso actual,
        Cola<Proceso> colaListos,
        int ciclosQuantum
    );
}
