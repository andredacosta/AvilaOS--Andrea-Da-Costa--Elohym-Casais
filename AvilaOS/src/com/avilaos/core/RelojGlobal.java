
package com.avilaos.core;

import java.util.concurrent.Semaphore;

public class RelojGlobal {

    private final Computador[] computadores;
    private final Semaphore mutex = new Semaphore(1, true);

    private long cicloActual;
    private long duracionCicloMs;
    private boolean pausado;

    public RelojGlobal(
            Computador[] computadores,
            long duracionCicloMs) {

        if (computadores == null
                || computadores.length < 2
                || duracionCicloMs <= 0) {
            throw new IllegalArgumentException(
                    "Configuracion del reloj invalida");
        }

        for (int i = 0; i < computadores.length; i++) {

            if (computadores[i] == null) {
                throw new IllegalArgumentException(
                        "Computador nulo");
            }

            for (int j = 0; j < i; j++) {
                if (computadores[i] == computadores[j]
                        || computadores[i].getId()
                        == computadores[j].getId()) {
                    throw new IllegalArgumentException(
                            "Computador duplicado");
                }
            }
        }

        this.computadores = computadores.clone();
        this.duracionCicloMs = duracionCicloMs;
        this.cicloActual = 0;
        this.pausado = true;
    }

    public void avanzarCiclo() {

        mutex.acquireUninterruptibly();

        try {

            Thread[] hilos =
                    new Thread[computadores.length];

            Throwable[] errores =
                    new Throwable[computadores.length];

            // Preparar un hilo por computador.
            for (int i = 0; i < computadores.length; i++) {

                final int indice = i;
                final ComponenteReloj componente =
                        computadores[i];

                hilos[i] = new Thread(() -> {

                    try {
                        componente.ejecutarCiclo();

                    } catch (Throwable error) {
                        errores[indice] = error;
                    }

                }, "AvilaOS-PC-" + computadores[i].getId());
            }

            // Iniciar todos los computadores.
            for (Thread hilo : hilos) {
                hilo.start();
            }

            // Barrera: esperar a que todos terminen.
            boolean interrumpido = false;

            for (Thread hilo : hilos) {

                boolean terminado = false;

                while (!terminado) {
                    try {
                        hilo.join();
                        terminado = true;

                    } catch (InterruptedException ex) {
                        interrumpido = true;
                    }
                }
            }

            // Propagar errores de ejecucion.
            for (Throwable error : errores) {

                if (error != null) {

                    if (interrumpido) {
                        Thread.currentThread().interrupt();
                    }

                    throw new IllegalStateException(
                            "Error al ejecutar un ciclo",
                            error);
                }
            }

            // Solo avanzar cuando todos terminaron.
            cicloActual++;

            if (interrumpido) {
                Thread.currentThread().interrupt();
            }

        } finally {
            mutex.release();
        }
    }

    public void iniciar() {

        mutex.acquireUninterruptibly();

        try {
            pausado = false;

        } finally {
            mutex.release();
        }
    }

    public void pausar() {

        mutex.acquireUninterruptibly();

        try {
            pausado = true;

        } finally {
            mutex.release();
        }
    }

    public void ejecutarAutomaticamente()
            throws InterruptedException {

        while (!Thread.currentThread().isInterrupted()) {

            boolean ejecutar;
            long duracion;

            mutex.acquire();

            try {
                ejecutar = !pausado;
                duracion = duracionCicloMs;

            } finally {
                mutex.release();
            }

            if (ejecutar) {
                avanzarCiclo();
            }

            Thread.sleep(duracion);
        }
    }

    public long getCicloActual() {

        mutex.acquireUninterruptibly();

        try {
            return cicloActual;

        } finally {
            mutex.release();
        }
    }

    public boolean estaPausado() {

        mutex.acquireUninterruptibly();

        try {
            return pausado;

        } finally {
            mutex.release();
        }
    }

    public void setDuracionCicloMs(long nuevaDuracion) {

        if (nuevaDuracion <= 0) {
            throw new IllegalArgumentException(
                    "Duracion invalida");
        }

        mutex.acquireUninterruptibly();

        try {
            duracionCicloMs = nuevaDuracion;

        } finally {
            mutex.release();
        }
    }
}
