
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.FCFS;
import com.avilaos.scheduler.RoundRobin;

public class PruebaSimulacion {

    public static void main(String[] args) {

        // Crear dos computadores con 100 unidades de RAM
        Computador pc1 = new Computador(
                1, 100, new FCFS());

        Computador pc2 = new Computador(
                2, 100, new RoundRobin(2));

        // Procesos del computador 1 (FCFS)
        Proceso p1 = new Proceso(
                new PCB(1, "Proceso A", 1,
                        TipoProceso.CPU_BOUND,
                        2, 30, 20, 5));

        // Deadline de B = 2 ciclos
        // Debe vencer mientras espera a que A termine
        Proceso p2 = new Proceso(
                new PCB(2, "Proceso B", 1,
                        TipoProceso.CPU_BOUND,
                        1, 40, 2, 3));

        // Procesos del computador 2 (Round Robin)
        Proceso p3 = new Proceso(
                new PCB(3, "Proceso C", 2,
                        TipoProceso.CPU_BOUND,
                        3, 25, 20, 6));

        Proceso p4 = new Proceso(
                new PCB(4, "Proceso D", 2,
                        TipoProceso.CPU_BOUND,
                        2, 35, 20, 4));

        // Agregar procesos a sus computadores
        pc1.agregarProceso(p1);
        pc1.agregarProceso(p2);

        pc2.agregarProceso(p3);
        pc2.agregarProceso(p4);

        // Crear reloj global
        RelojGlobal reloj = new RelojGlobal(
                new Computador[]{pc1, pc2}, 500);

        // Ejecutar 12 ciclos de simulacion
        for (int i = 0; i < 12; i++) {

            reloj.avanzarCiclo();

            System.out.println(
                    "========== CICLO "
                    + reloj.getCicloActual()
                    + " ==========");

            mostrarEstado(pc1);
            mostrarEstado(pc2);
        }

        // Resultados de la prueba
        System.out.println();
        System.out.println("========== RESULTADOS ==========");

        System.out.println(
                "Proceso A terminado: "
                + p1.estaTerminado());

        System.out.println(
                "Proceso B vencido: "
                + p2.vencioPorDeadline());

        System.out.println(
                "Instrucciones ejecutadas por B: "
                + p2.getPcb().getProgramCounter());

        System.out.println(
                "Deadline restante de B: "
                + p2.getPcb().getDeadline());

        System.out.println(
                "RAM final computador 1: "
                + pc1.getMemoria().getMemoriaUsada());

        System.out.println(
                "RAM final computador 2: "
                + pc2.getMemoria().getMemoriaUsada());
    }

    private static void mostrarEstado(Computador pc) {

        Proceso actual = pc.getCpu().getProcesoActual();

        String nombre = actual == null
                ? "LIBRE"
                : actual.getPcb().getNombre()
                  + " (PC="
                  + actual.getPcb().getProgramCounter()
                  + ")";

        System.out.println(
                "Computador " + pc.getId()
                + " | CPU: " + nombre
                + " | RAM usada: "
                + pc.getMemoria().getMemoriaUsada()
                + " | Listos: "
                + pc.getProcesosListos()
                + " | Nuevos: "
                + pc.getProcesosNuevos());
    }
}
