
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.FCFS;

public class PruebaEntradaSalida {

    public static void main(String[] args) {

        Computador pc = new Computador(
                1, 100, new FCFS());

        Proceso procesoIO = new Proceso(
                new PCB(1, "Proceso IO", 1,
                        TipoProceso.IO_BOUND,
                        1, 30, 30, 5));

        Proceso procesoCPU = new Proceso(
                new PCB(2, "Proceso CPU", 1,
                        TipoProceso.CPU_BOUND,
                        2, 20, 30, 4));

        pc.agregarProceso(procesoIO);
        pc.agregarProceso(procesoCPU);

       RelojGlobal reloj = new RelojGlobal(
        new Computador[]{
            pc,
            new Computador(99, 100, new FCFS())
        }, 500);

        for (int i = 0; i < 20; i++) {

            reloj.avanzarCiclo();

            System.out.println(
                    "========== CICLO "
                    + reloj.getCicloActual()
                    + " ==========");

            System.out.println(
                    "IO | Estado: "
                    + procesoIO.getPcb().getEstado()
                    + " | PC: "
                    + procesoIO.getPcb().getProgramCounter()
                    + " | E/S restante: "
                    + procesoIO.getPcb().getCiclosIORestantes());

            System.out.println(
                    "CPU | Estado: "
                    + procesoCPU.getPcb().getEstado()
                    + " | PC: "
                    + procesoCPU.getPcb().getProgramCounter());

            System.out.println(
                    "Cola bloqueados: "
                    + pc.getProcesosBloqueados()
                    + " | Cola listos: "
                    + pc.getProcesosListos()
                    + " | RAM usada: "
                    + pc.getMemoria().getMemoriaUsada());
        }

        System.out.println();
        System.out.println("========== RESULTADOS ==========");

        System.out.println(
                "Proceso IO terminado: "
                + procesoIO.estaTerminado());

        System.out.println(
                "Proceso CPU terminado: "
                + procesoCPU.estaTerminado());

        System.out.println(
                "RAM final: "
                + pc.getMemoria().getMemoriaUsada());
    }
}
