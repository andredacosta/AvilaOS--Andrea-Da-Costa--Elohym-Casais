
package com.avilaos.core;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.MotivoBloqueo;
import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.TipoProceso;
import com.avilaos.sync.SemaforoSimulado;

public class PruebaSemaforoSimulado {

    private static int pruebasPasadas = 0;
    private static int pruebasFallidas = 0;

    public static void main(String[] args) {

        probarTransferencia();
        probarProcesoVencidoEnCola();
        probarPermisoReservadoVencido();

        System.out.println();
        System.out.println("========== RESULTADOS ==========");
        System.out.println("PASS: " + pruebasPasadas);
        System.out.println("FAIL: " + pruebasFallidas);

        System.out.println(
                "PRUEBA SEMAFOROS: "
                + (pruebasFallidas == 0 ? "PASS" : "FAIL"));
    }

    private static void comprobar(
            String descripcion, boolean condicion) {

        System.out.println(
                descripcion + ": "
                + (condicion ? "PASS" : "FAIL"));

        if (condicion) {
            pruebasPasadas++;
        } else {
            pruebasFallidas++;
        }
    }

    private static Proceso crearProceso(int id) {

        Proceso proceso = new Proceso(
                new PCB(id, "Proceso " + id, 1,
                        TipoProceso.CONSUMIDOR,
                        1, 20, 30, 5));

        proceso.getPcb().setEstado(
                EstadoProceso.EJECUCION);

        return proceso;
    }

    private static void probarTransferencia() {

        System.out.println(
                "========== TRANSFERENCIA ==========");

        SemaforoSimulado semaforo =
                new SemaforoSimulado("llenos_B1", 0);

        Proceso consumidor = crearProceso(1);

        boolean adquirido = semaforo.semWait(
                consumidor, MotivoBloqueo.BUFFER_VACIO);

        comprobar(
                "Consumidor se bloquea",
                !adquirido
                && consumidor.getPcb().getEstado()
                == EstadoProceso.BLOQUEADO);

        Proceso despertado = semaforo.semSignal();

        comprobar(
                "Permiso reservado",
                despertado == consumidor
                && semaforo.getPermisosTransferidos() == 1);

        despertado.getPcb().desbloquear();
        despertado.getPcb().setEstado(
                EstadoProceso.EJECUCION);

        boolean segundoWait = semaforo.semWait(
                consumidor, MotivoBloqueo.BUFFER_VACIO);

        comprobar(
                "Consumidor utiliza permiso transferido",
                segundoWait
                && semaforo.getPermisosTransferidos() == 0
                && semaforo.getValorActual() == 0);

        System.out.println();
    }

    private static void probarProcesoVencidoEnCola() {

        System.out.println(
                "========== VENCIDO EN COLA ==========");

        SemaforoSimulado semaforo =
                new SemaforoSimulado("vacios_B2", 0);

        Proceso productor = crearProceso(2);

        semaforo.semWait(
                productor, MotivoBloqueo.BUFFER_LLENO);

        productor.vencerDeadline();

        Proceso despertado = semaforo.semSignal();

        comprobar(
                "No despierta proceso vencido",
                despertado == null);

        comprobar(
                "Permiso queda disponible",
                semaforo.getValorActual() == 1);

        comprobar(
                "Cola sin procesos pendientes",
                semaforo.getProcesosEsperando() == 0);

        System.out.println();
    }

    private static void probarPermisoReservadoVencido() {

        System.out.println(
                "========== PERMISO RESERVADO ==========");

        SemaforoSimulado semaforo =
                new SemaforoSimulado("llenos_B3", 0);

        Proceso consumidor = crearProceso(3);

        semaforo.semWait(
                consumidor, MotivoBloqueo.BUFFER_VACIO);

        Proceso despertado = semaforo.semSignal();

        comprobar(
                "Se reserva el permiso",
                despertado == consumidor
                && semaforo.getPermisosTransferidos() == 1);

        consumidor.vencerDeadline();

        // Esta prueba verifica la limpieza que hace
        // actualmente semWait al revisar reservas.
        Proceso otro = crearProceso(4);

        boolean adquirido = semaforo.semWait(
                otro, MotivoBloqueo.BUFFER_VACIO);

        comprobar(
                "Permiso de proceso vencido recuperado",
                adquirido
                && semaforo.getPermisosTransferidos() == 0
                && semaforo.getValorActual() == 0);

        System.out.println();
    }
}
