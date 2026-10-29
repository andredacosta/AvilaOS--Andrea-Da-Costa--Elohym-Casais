
package com.avilaos.core;

import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.sync.BufferSimulado;

public class ControlProductorConsumidor {

    private final BufferSimulado buffer;

    public ControlProductorConsumidor(BufferSimulado buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException(
                    "El buffer no puede ser nulo");
        }

        this.buffer = buffer;
    }

    public boolean ejecutarOperacion(Proceso proceso) {

        if (proceso == null) {
            throw new IllegalArgumentException(
                    "Proceso invalido");
        }

        TipoProceso tipo = proceso.getPcb().getTipo();

        if (tipo == TipoProceso.PRODUCTOR) {

            String elemento =
                    "P" + proceso.getPcb().getId()
                    + "-I" + proceso.getPcb().getProgramCounter();

            return buffer.intentarProducir(elemento);
        }

        if (tipo == TipoProceso.CONSUMIDOR) {

            BufferSimulado.ResultadoConsumo resultado =
                    buffer.intentarConsumir();

            return resultado.isExitoso();
        }

        throw new IllegalArgumentException(
                "El proceso no es productor ni consumidor");
    }

    public BufferSimulado getBuffer() {
        return buffer;
    }
}
