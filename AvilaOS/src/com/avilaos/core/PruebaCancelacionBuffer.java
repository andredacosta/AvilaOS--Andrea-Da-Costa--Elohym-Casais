
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.model.EstadoProceso;
import com.avilaos.scheduler.FCFS;
import com.avilaos.sync.BufferSimulado;

public class PruebaCancelacionBuffer {

    public static void main(String[] args) {

        BufferSimulado buffer =
                new BufferSimulado("TEST-CANCEL", 2, 1);

        ControlProductorConsumidor control =
                new ControlProductorConsumidor(buffer);

        Computador pc1 =
                new Computador(1, 100, new FCFS());

        Computador pc2 =
                new Computador(2, 100, new FCFS());

        pc1.configurarControlBuffer(control);
        pc2.configurarControlBuffer(control);

        pc1.configurarLatenciaRed(0);
        pc2.configurarLatenciaRed(0);

        // Vence despues de adquirir el primer permiso,
        // antes de completar el protocolo.
        Proceso productorCorto = new Proceso(
                new PCB(1, "Productor corto", 1,
                        TipoProceso.PRODUCTOR,
                        1, 20, 1, 3));

        pc1.agregarProceso(productorCorto);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc1, pc2}, 500);

        reloj.avanzarCiclo();

        System.out.println(
                "Productor vencido: "
                + productorCorto.vencioPorDeadline());

        System.out.println(
                "Permisos vacios tras vencimiento: "
                + buffer.getVacios().getValorActual());

        System.out.println(
                "Permisos mutex: "
                + buffer.getMutex().getValorActual());

        System.out.println(
                "Elementos en buffer: "
                + buffer.getCantidad());

        boolean correcto =
                productorCorto.vencioPorDeadline()
                && buffer.getVacios().getValorActual() == 2
                && buffer.getMutex().getValorActual() == 1
                && buffer.getCantidad() == 0;

        System.out.println(
                "PRUEBA CANCELACION: "
                + (correcto ? "PASS" : "FAIL"));
    }
}
