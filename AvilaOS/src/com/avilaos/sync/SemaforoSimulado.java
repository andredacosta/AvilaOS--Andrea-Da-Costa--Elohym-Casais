
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
            Proceso proceso,
            MotivoBloqueo motivo) {

        if (proceso == null || motivo == null
                || motivo == MotivoBloqueo.NINGUNO) {
            throw new IllegalArgumentException(
                    "Argumentos invalidos");
        }

        if (proceso.getPcb().getEstado()
                != EstadoProceso.EJECUCION) {
            throw new IllegalStateException(
                    "El proceso debe estar en ejecucion");
        }

        if (consumirPermisoTransferido(proceso)) {
            return true;
        }

        if (valor > 0) {
            valor--;
            return true;
        }

        if (!estaEsperando(proceso)) {
            proceso.getPcb().bloquear(motivo);
            colaEspera.encolar(proceso);
        }

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

            permisosTransferidos.encolar(proceso);
            return proceso;
        }

        valor++;
        return null;
    }

    public synchronized void cancelarEspera(
            Proceso proceso) {

        if (proceso == null) {
            return;
        }

        int esperando = colaEspera.getTamanio();

        for (int i = 0; i < esperando; i++) {

            Proceso actual = colaEspera.desencolar();

            if (actual != proceso) {
                colaEspera.encolar(actual);
            }
        }

        int transferidos =
                permisosTransferidos.getTamanio();

        int recuperados = 0;

        for (int i = 0; i < transferidos; i++) {

            Proceso actual =
                    permisosTransferidos.desencolar();

            if (actual == proceso) {
                recuperados++;
            } else {
                permisosTransferidos.encolar(actual);
            }
        }

        for (int i = 0; i < recuperados; i++) {
            semSignal();
        }
    }

    private boolean estaEsperando(Proceso buscado) {

        int cantidad = colaEspera.getTamanio();
        boolean encontrado = false;

        for (int i = 0; i < cantidad; i++) {

            Proceso actual = colaEspera.desencolar();

            if (actual == buscado) {
                encontrado = true;
            }

            colaEspera.encolar(actual);
        }

        return encontrado;
    }

    private boolean consumirPermisoTransferido(
            Proceso buscado) {

        int cantidad =
                permisosTransferidos.getTamanio();

        boolean encontrado = false;

        for (int i = 0; i < cantidad; i++) {

            Proceso actual =
                    permisosTransferidos.desencolar();

            if (actual == buscado && !encontrado) {
                encontrado = true;

            } else if (actual.estaTerminado()) {
                semSignal();

            } else {
                permisosTransferidos.encolar(actual);
            }
        }

        return encontrado;
    }

    public synchronized boolean tienePermisoTransferido(
            Proceso buscado) {

        int cantidad =
                permisosTransferidos.getTamanio();

        boolean encontrado = false;

        for (int i = 0; i < cantidad; i++) {

            Proceso actual =
                    permisosTransferidos.desencolar();

            if (actual == buscado) {
                encontrado = true;
            }

            permisosTransferidos.encolar(actual);
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
}
