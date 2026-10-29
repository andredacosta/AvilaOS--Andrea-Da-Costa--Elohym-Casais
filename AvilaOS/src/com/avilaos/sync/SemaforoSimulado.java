
package com.avilaos.sync;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.MotivoBloqueo;
import com.avilaos.model.Proceso;
import com.avilaos.structures.Cola;

public class SemaforoSimulado {

    private final String nombre;
    private final Cola<Proceso> colaEspera;
    private final Cola<Proceso> permisosTransferidos;

    private int valor;

    public SemaforoSimulado(String nombre, int valorInicial) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "Nombre de semaforo invalido");
        }

        if (valorInicial < 0) {
            throw new IllegalArgumentException(
                    "Valor inicial invalido");
        }

        this.nombre = nombre;
        this.valor = valorInicial;
        this.colaEspera = new Cola<>();
        this.permisosTransferidos = new Cola<>();
    }

    public synchronized boolean semWait(
            Proceso proceso, MotivoBloqueo motivo) {

        if (proceso == null) {
            throw new IllegalArgumentException(
                    "Proceso invalido");
        }

        if (motivo == null
                || motivo == MotivoBloqueo.NINGUNO) {
            throw new IllegalArgumentException(
                    "Motivo invalido");
        }

        if (proceso.getPcb().getEstado()
                != EstadoProceso.EJECUCION) {
            throw new IllegalStateException(
                    "El proceso debe estar EJECUCION");
        }

        // Si recibio un permiso por transferencia,
        // puede continuar sin descontarlo otra vez.
        if (consumirPermisoTransferido(proceso)) {
            return true;
        }

        // Adquirir un permiso disponible.
        if (valor > 0) {
            valor--;
            return true;
        }

        // Bloquear el proceso simulado.
        proceso.getPcb().bloquear(motivo);
        colaEspera.encolar(proceso);

        return false;
    }

    public synchronized Proceso semSignal() {

        while (!colaEspera.estaVacia()) {

            Proceso proceso = colaEspera.desencolar();

            if (proceso.estaTerminado()) {
                continue;
            }

            if (proceso.getPcb().getEstado()
                    != EstadoProceso.BLOQUEADO) {
                continue;
            }

            // Reservar el permiso para este proceso.
            permisosTransferidos.encolar(proceso);

            // El nucleo debera moverlo a LISTO.
            return proceso;
        }

        valor++;
        return null;
    }

    private boolean consumirPermisoTransferido(
            Proceso procesoBuscado) {

        int cantidad = permisosTransferidos.getTamanio();
        boolean encontrado = false;

        for (int i = 0; i < cantidad; i++) {

            Proceso proceso =
                    permisosTransferidos.desencolar();

            if (proceso.estaTerminado()) {

                // Liberar el permiso reservado que
                // ya no podra utilizar.
                valor++;

            } else if (proceso == procesoBuscado
                    && !encontrado) {

                encontrado = true;

            } else {

                permisosTransferidos.encolar(proceso);
            }
        }

        return encontrado;
    }

    public synchronized int getValorActual() {
        return valor;
    }

    public synchronized int getProcesosEsperando() {
        return colaEspera.getTamanio();
    }

    public synchronized int getPermisosTransferidos() {
        return permisosTransferidos.getTamanio();
    }

    public String getNombre() {
        return nombre;
    }

public synchronized boolean tienePermisoTransferido(
        Proceso buscado) {

    int cantidad = permisosTransferidos.getTamanio();
    boolean encontrado = false;

    for (int i = 0; i < cantidad; i++) {

        Proceso proceso = permisosTransferidos.desencolar();

        if (proceso == buscado && !proceso.estaTerminado()) {
            encontrado = true;
        }

        permisosTransferidos.encolar(proceso);
    }

    return encontrado;
}

}
