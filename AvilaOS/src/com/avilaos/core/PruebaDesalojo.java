
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.EDF;
import com.avilaos.scheduler.Prioridades;
import com.avilaos.scheduler.FCFS;

public class PruebaDesalojo {

    public static void main(String[] args) {

        probarEDF();
        probarPrioridades();
    }

    private static void comprobar(
            String nombre, boolean condicion) {

        System.out.println(
                nombre + ": "
                + (condicion ? "PASS" : "FAIL"));
    }

    private static void probarEDF() {

        System.out.println(
                "========== DESALOJO EDF ==========");

        // Computador principal con EDF
        Computador pc = new Computador(
                1, 100, new EDF());

        // Segundo computador para el reloj global
        Computador pcAuxiliar = new Computador(
                99, 100, new FCFS());

        Proceso a = new Proceso(
                new PCB(1, "A", 1,
                        TipoProceso.CPU_BOUND,
                        3, 20, 30, 8));

        pc.agregarProceso(a);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc, pcAuxiliar}, 500);

        // A comienza a ejecutarse
        reloj.avanzarCiclo();

        comprobar(
                "A comienza",
                a.getPcb().getProgramCounter() == 1);

        // Llega B con deadline mas cercano
        Proceso b = new Proceso(
                new PCB(2, "B", 1,
                        TipoProceso.CPU_BOUND,
                        2, 20, 10, 3));

        pc.agregarProceso(b);

        reloj.avanzarCiclo();

        // EDF debe desalojar A y ejecutar B
        comprobar(
                "EDF desaloja A",
                a.getPcb().getProgramCounter() == 1
                && b.getPcb().getProgramCounter() == 1);

        System.out.println(
                "A PC=" + a.getPcb().getProgramCounter()
                + " | B PC="
                + b.getPcb().getProgramCounter());

        for (int i = 0; i < 15; i++) {
            reloj.avanzarCiclo();
        }

        comprobar(
                "EDF procesos terminados",
                a.estaTerminado()
                && b.estaTerminado()
                && !a.vencioPorDeadline()
                && !b.vencioPorDeadline());

        comprobar(
                "EDF libera RAM",
                pc.getMemoria().getMemoriaUsada() == 0);

        System.out.println();
    }

    private static void probarPrioridades() {

        System.out.println(
                "========== DESALOJO PRIORIDADES ==========");

        // Computador principal con Prioridades
        Computador pc = new Computador(
                2, 100, new Prioridades());

        // Segundo computador para el reloj global
        Computador pcAuxiliar = new Computador(
                99, 100, new FCFS());

        Proceso a = new Proceso(
                new PCB(3, "A", 2,
                        TipoProceso.CPU_BOUND,
                        5, 20, 30, 8));

        pc.agregarProceso(a);

        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc, pcAuxiliar}, 500);

        reloj.avanzarCiclo();

        comprobar(
                "A comienza",
                a.getPcb().getProgramCounter() == 1);

        // B tiene mayor prioridad (numero menor)
        Proceso b = new Proceso(
                new PCB(4, "B", 2,
                        TipoProceso.CPU_BOUND,
                        1, 20, 30, 3));

        pc.agregarProceso(b);

        reloj.avanzarCiclo();

        comprobar(
                "Prioridades desaloja A",
                a.getPcb().getProgramCounter() == 1
                && b.getPcb().getProgramCounter() == 1);

        System.out.println(
                "A PC=" + a.getPcb().getProgramCounter()
                + " | B PC="
                + b.getPcb().getProgramCounter());

        for (int i = 0; i < 15; i++) {
            reloj.avanzarCiclo();
        }

        comprobar(
                "Prioridades procesos terminados",
                a.estaTerminado()
                && b.estaTerminado()
                && !a.vencioPorDeadline()
                && !b.vencioPorDeadline());

        comprobar(
                "Prioridades libera RAM",
                pc.getMemoria().getMemoriaUsada() == 0);
    }
}
