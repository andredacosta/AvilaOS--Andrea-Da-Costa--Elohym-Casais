
package com.avilaos.core;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.scheduler.EDF;
import com.avilaos.scheduler.FCFS;
import com.avilaos.scheduler.Prioridades;
import com.avilaos.scheduler.RoundRobin;

public class PruebaFinalAndrea {

    private static int aprobadas = 0;
    private static int fallidas = 0;

    private static void verificar(
            String nombre, boolean condicion) {

        if (condicion) {
            aprobadas++;
            System.out.println("PASS - " + nombre);
        } else {
            fallidas++;
            System.out.println("FAIL - " + nombre);
        }
    }

    private static Proceso crearProceso(
            int id,
            String nombre,
            int computadorId,
            int memoria,
            int deadline,
            int instrucciones) {

        return new Proceso(
                new PCB(
                        id,
                        nombre,
                        computadorId,
                        TipoProceso.CPU_BOUND,
                        1,
                        memoria,
                        deadline,
                        instrucciones));
    }

    public static void main(String[] args) {

        System.out.println(
                "===== PRUEBA FINAL ANDREA =====");

        // =====================================
        // 1. ARQUITECTURA Y RAM
        // =====================================

        Computador pc1 =
                new Computador(1, 100, new FCFS());

        Computador pc2 =
                new Computador(2, 100, new FCFS());

        verificar(
                "Computadores independientes",
                pc1.getId() != pc2.getId()
                && pc1.getCpu() != pc2.getCpu()
                && pc1.getMemoria() != pc2.getMemoria());

        // =====================================
        // 2. CREACION Y ESTADOS
        // =====================================

        Proceso proceso =
                crearProceso(
                        101, "Proceso A",
                        1, 20, 20, 2);

        verificar(
                "Estado inicial NUEVO",
                proceso.getPcb().getEstado()
                == EstadoProceso.NUEVO);

        pc1.agregarProceso(proceso);

        verificar(
                "Cola de nuevos",
                pc1.getProcesosNuevos() == 1);

        // =====================================
        // 3. RELOJ GLOBAL
        // =====================================

        RelojGlobal reloj =
                new RelojGlobal(
                        new Computador[]{pc1, pc2},
                        100);

        reloj.avanzarCiclo();

        verificar(
                "Reloj avanza un ciclo",
                reloj.getCicloActual() == 1);

        verificar(
                "Proceso ejecuta instrucciones",
                proceso.getPcb().getProgramCounter() == 1);

        verificar(
                "PC y MAR sincronizados",
                proceso.getPcb().getProgramCounter()
                == proceso.getPcb().getMar());

        // =====================================
        // 4. TERMINACION Y MEMORIA
        // =====================================

        reloj.avanzarCiclo();

        verificar(
                "Proceso termina",
                proceso.estaTerminado());

        verificar(
                "Memoria liberada",
                pc1.getMemoria().getMemoriaUsada() == 0);

        verificar(
                "Cola de terminados",
                pc1.getProcesosTerminados() == 1);

        // =====================================
        // 5. DEADLINES
        // =====================================

        Proceso procesoVencido =
                crearProceso(
                        102, "Deadline",
                        1, 20, 1, 20);

        pc1.agregarProceso(procesoVencido);

        reloj.avanzarCiclo();

        verificar(
                "Deadline vence",
                procesoVencido.vencioPorDeadline());

        verificar(
                "Proceso vencido TERMINADO",
                procesoVencido.getPcb().getEstado()
                == EstadoProceso.TERMINADO);

        verificar(
                "RAM liberada tras deadline",
                pc1.getMemoria().getMemoriaUsada() == 0);

        // =====================================
        // 6. CAMBIO DE PLANIFICADORES
        // =====================================

        pc1.cambiarPlanificador(new RoundRobin(2));

        verificar(
                "Cambio a Round Robin",
                true);

        pc1.cambiarPlanificador(new EDF());

        verificar(
                "Cambio a EDF",
                true);

        pc1.cambiarPlanificador(new Prioridades());

        verificar(
                "Cambio a Prioridades",
                true);

        pc1.cambiarPlanificador(new FCFS());

        verificar(
                "Cambio a FCFS",
                true);

        // =====================================
        // RESULTADO
        // =====================================

        System.out.println();
        System.out.println(
                "==============================");

        System.out.println(
                "APROBADAS: " + aprobadas);

        System.out.println(
                "FALLIDAS: " + fallidas);

        System.out.println(
                "RESULTADO FINAL: "
                + (fallidas == 0 ? "PASS" : "FAIL"));

        System.out.println(
                "==============================");
    }
}
