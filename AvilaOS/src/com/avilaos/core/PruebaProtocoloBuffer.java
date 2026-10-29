
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.FCFS;
import com.avilaos.sync.BufferSimulado;

public class PruebaProtocoloBuffer {

    public static void main(String[] args) {

        BufferSimulado buffer =
                new BufferSimulado("B-TEST", 2, 1);

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

        Proceso productor = new Proceso(
                new PCB(1, "Productor", 1,
                        TipoProceso.PRODUCTOR,
                        1, 20, 100, 3));

        Proceso consumidor = new Proceso(
                new PCB(2, "Consumidor", 2,
                        TipoProceso.CONSUMIDOR,
                        1, 20, 100, 3));

        pc1.agregarProceso(productor);
        pc2.agregarProceso(consumidor);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc1, pc2}, 500);

        for (int i = 0; i < 40; i++) {

            reloj.avanzarCiclo();

            System.out.println(
                    "Ciclo " + reloj.getCicloActual()
                    + " | Productor PC="
                    + productor.getPcb().getProgramCounter()
                    + " | Consumidor PC="
                    + consumidor.getPcb().getProgramCounter()
                    + " | Buffer=" + buffer.getCantidad());
        }

        System.out.println();
        System.out.println("========== RESULTADOS ==========");

        System.out.println(
                "Elementos producidos: "
                + control.getProducidos());

        System.out.println(
                "Elementos consumidos: "
                + control.getConsumidos());

        System.out.println(
                "PC productor: "
                + productor.getPcb().getProgramCounter());

        System.out.println(
                "PC consumidor: "
                + consumidor.getPcb().getProgramCounter());

        System.out.println(
                "Ciclos SO PC1: "
                + pc1.getCpu().getCiclosSistema());

        System.out.println(
                "Ciclos SO PC2: "
                + pc2.getCpu().getCiclosSistema());

        boolean correcto =
                control.getProducidos() == 3
                && control.getConsumidos() == 3
                && productor.estaTerminado()
                && consumidor.estaTerminado()
                && !productor.vencioPorDeadline()
                && !consumidor.vencioPorDeadline()
                && buffer.getCantidad() == 0;

        System.out.println(
                "PRUEBA PROTOCOLO: "
                + (correcto ? "PASS" : "FAIL"));
    }
}
