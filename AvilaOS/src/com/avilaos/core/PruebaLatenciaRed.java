
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.model.MotivoBloqueo;
import com.avilaos.scheduler.FCFS;
import com.avilaos.sync.BufferSimulado;

public class PruebaLatenciaRed {

    public static void main(String[] args) {

        BufferSimulado buffer =
                new BufferSimulado("RED-1", 2, 1);

        ControlProductorConsumidor control =
                new ControlProductorConsumidor(buffer);

        Computador pc1 =
                new Computador(1, 100, new FCFS());

        Computador pc2 =
                new Computador(2, 100, new FCFS());

        pc1.configurarControlBuffer(control);
        pc2.configurarControlBuffer(control);

        pc1.configurarLatenciaRed(3);
        pc2.configurarLatenciaRed(3);

        Proceso productor = new Proceso(
                new PCB(1, "Productor Local", 1,
                        TipoProceso.PRODUCTOR,
                        1, 20, 80, 5));

        Proceso consumidor = new Proceso(
                new PCB(2, "Consumidor Remoto", 2,
                        TipoProceso.CONSUMIDOR,
                        1, 20, 80, 5));

        pc1.agregarProceso(productor);
        pc2.agregarProceso(consumidor);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc1, pc2}, 500);

        int ciclosLatencia = 0;

        for (int i = 0; i < 45; i++) {

            reloj.avanzarCiclo();

            if (consumidor.getPcb().getMotivoBloqueo()
                    == MotivoBloqueo.LATENCIA_RED) {
                ciclosLatencia++;
            }

            System.out.println(
                    "Ciclo " + reloj.getCicloActual()
                    + " | Buffer=" + buffer.getCantidad()
                    + " | Productor PC="
                    + productor.getPcb().getProgramCounter()
                    + " | Consumidor PC="
                    + consumidor.getPcb().getProgramCounter()
                    + " | Estado consumidor="
                    + consumidor.getPcb().getEstado()
                    + " | Motivo="
                    + consumidor.getPcb().getMotivoBloqueo()
                    + " | Latencia restante="
                    + consumidor.getPcb()
                            .getCiclosLatenciaRestantes());
        }

        boolean correcto =
                productor.estaTerminado()
                && consumidor.estaTerminado()
                && !productor.vencioPorDeadline()
                && !consumidor.vencioPorDeadline()
                && buffer.getCantidad() == 0
                && pc1.getMemoria().getMemoriaUsada() == 0
                && pc2.getMemoria().getMemoriaUsada() == 0
                && ciclosLatencia > 0;

        System.out.println();
        System.out.println("========== RESULTADOS ==========");

        System.out.println(
                "Productor terminado: "
                + productor.estaTerminado());

        System.out.println(
                "Consumidor terminado: "
                + consumidor.estaTerminado());

        System.out.println(
                "Ciclos observados en LATENCIA_RED: "
                + ciclosLatencia);

        System.out.println(
                "Buffer final: " + buffer.getCantidad());

        System.out.println(
                "RAM PC1: "
                + pc1.getMemoria().getMemoriaUsada());

        System.out.println(
                "RAM PC2: "
                + pc2.getMemoria().getMemoriaUsada());

        System.out.println(
                "PRUEBA LATENCIA: "
                + (correcto ? "PASS" : "FAIL"));
    }
}
