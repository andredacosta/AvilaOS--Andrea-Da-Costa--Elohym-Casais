
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.FCFS;
import com.avilaos.sync.BufferSimulado;

public class PruebaProductorConsumidor {

    public static void main(String[] args) {

        BufferSimulado buffer =
                new BufferSimulado("B1", 2, 1);

        ControlProductorConsumidor control =
                new ControlProductorConsumidor(buffer);

        Computador pc1 =
                new Computador(1, 100, new FCFS());

        Computador pc2 =
                new Computador(2, 100, new FCFS());

        // Los dos computadores comparten el mismo buffer.
        pc1.configurarControlBuffer(control);
        pc2.configurarControlBuffer(control);

        Proceso productor = new Proceso(
                new PCB(1, "Productor", 1,
                        TipoProceso.PRODUCTOR,
                        1, 20, 30, 5));

        Proceso consumidor = new Proceso(
                new PCB(2, "Consumidor", 2,
                        TipoProceso.CONSUMIDOR,
                        1, 20, 30, 5));

        pc1.agregarProceso(productor);
        pc2.agregarProceso(consumidor);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc1, pc2}, 500);

        for (int i = 0; i < 15; i++) {

            reloj.avanzarCiclo();

            System.out.println(
                    "Ciclo " + reloj.getCicloActual()
                    + " | Buffer=" + buffer.getCantidad()
                    + " | Productor PC="
                    + productor.getPcb().getProgramCounter()
                    + " Estado="
                    + productor.getPcb().getEstado()
                    + " | Consumidor PC="
                    + consumidor.getPcb().getProgramCounter()
                    + " Estado="
                    + consumidor.getPcb().getEstado());
        }

        boolean correcto =
                productor.estaTerminado()
                && consumidor.estaTerminado()
                && buffer.getCantidad() == 0
                && pc1.getMemoria().getMemoriaUsada() == 0
                && pc2.getMemoria().getMemoriaUsada() == 0;

        System.out.println();
        System.out.println("========== RESULTADOS ==========");
        System.out.println(
                "Productor terminado: "
                + productor.estaTerminado());
        System.out.println(
                "Consumidor terminado: "
                + consumidor.estaTerminado());
        System.out.println(
                "Buffer final: " + buffer.getCantidad());
        System.out.println(
                "RAM PC1: "
                + pc1.getMemoria().getMemoriaUsada());
        System.out.println(
                "RAM PC2: "
                + pc2.getMemoria().getMemoriaUsada());
        System.out.println(
                "PRUEBA PRODUCTOR-CONSUMIDOR: "
                + (correcto ? "PASS" : "FAIL"));
    }
}
