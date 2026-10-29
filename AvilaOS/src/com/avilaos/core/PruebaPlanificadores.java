
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.EDF;
import com.avilaos.scheduler.Prioridades;

public class PruebaPlanificadores {

    public static void main(String[] args) {

        probarEDF();
        probarPrioridades();
    }

    private static void probarEDF() {

        System.out.println("========== PRUEBA EDF ==========");

        Computador pc = new Computador(
                1, 100, new EDF());

        Proceso a = new Proceso(
                new PCB(1, "Proceso A", 1,
                        TipoProceso.CPU_BOUND,
                        3, 20, 20, 5));

        Proceso b = new Proceso(
                new PCB(2, "Proceso B", 1,
                        TipoProceso.CPU_BOUND,
                        2, 20, 10, 4));

        Proceso c = new Proceso(
                new PCB(3, "Proceso C", 1,
                        TipoProceso.CPU_BOUND,
                        1, 20, 15, 3));

        pc.agregarProceso(a);
        pc.agregarProceso(b);
        pc.agregarProceso(c);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc}, 500);

        for (int i = 0; i < 12; i++) {

            reloj.avanzarCiclo();

            System.out.println(
                    "Ciclo " + reloj.getCicloActual()
                    + " | A PC=" + a.getPcb().getProgramCounter()
                    + " | B PC=" + b.getPcb().getProgramCounter()
                    + " | C PC=" + c.getPcb().getProgramCounter()
                    + " | RAM=" + pc.getMemoria().getMemoriaUsada());
        }

        System.out.println(
                "EDF - B ejecuta primero: "
                + (b.getPcb().getProgramCounter() > 0));

        System.out.println();
    }

    private static void probarPrioridades() {

        System.out.println(
                "========== PRUEBA PRIORIDADES ==========");

        Computador pc = new Computador(
                2, 100, new Prioridades());

        Proceso a = new Proceso(
                new PCB(4, "Proceso A", 2,
                        TipoProceso.CPU_BOUND,
                        3, 20, 20, 5));

        Proceso b = new Proceso(
                new PCB(5, "Proceso B", 2,
                        TipoProceso.CPU_BOUND,
                        1, 20, 20, 4));

        Proceso c = new Proceso(
                new PCB(6, "Proceso C", 2,
                        TipoProceso.CPU_BOUND,
                        2, 20, 20, 3));

        pc.agregarProceso(a);
        pc.agregarProceso(b);
        pc.agregarProceso(c);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc}, 500);

        for (int i = 0; i < 12; i++) {

            reloj.avanzarCiclo();

            System.out.println(
                    "Ciclo " + reloj.getCicloActual()
                    + " | A PC=" + a.getPcb().getProgramCounter()
                    + " | B PC=" + b.getPcb().getProgramCounter()
                    + " | C PC=" + c.getPcb().getProgramCounter()
                    + " | RAM=" + pc.getMemoria().getMemoriaUsada());
        }

        System.out.println(
                "Prioridades - B ejecuta: "
                + (b.getPcb().getProgramCounter() > 0));
    }
}
