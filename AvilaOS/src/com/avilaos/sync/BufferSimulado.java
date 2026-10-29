
package com.avilaos.sync;

public class BufferSimulado {

    private final String idBuffer;
    private final int computadorAnfitrionId;
    private final Object[] almacenamiento;

    private int entrada;
    private int salida;
    private int cantidad;

    // Semaforos simulados del buffer acotado
    private final SemaforoSimulado mutex;
    private final SemaforoSimulado llenos;
    private final SemaforoSimulado vacios;

    public BufferSimulado(
            String idBuffer,
            int capacidad,
            int computadorAnfitrionId) {

        if (idBuffer == null || idBuffer.isBlank()) {
            throw new IllegalArgumentException(
                    "ID de buffer invalido");
        }

        if (capacidad <= 0 || computadorAnfitrionId <= 0) {
            throw new IllegalArgumentException(
                    "Configuracion de buffer invalida");
        }

        this.idBuffer = idBuffer;
        this.computadorAnfitrionId = computadorAnfitrionId;
        this.almacenamiento = new Object[capacidad];

        this.entrada = 0;
        this.salida = 0;
        this.cantidad = 0;

        this.mutex = new SemaforoSimulado(
                "mutex_" + idBuffer, 1);

        this.llenos = new SemaforoSimulado(
                "llenos_" + idBuffer, 0);

        this.vacios = new SemaforoSimulado(
                "vacios_" + idBuffer, capacidad);
    }

    // Operacion basica de produccion.
    // Por ahora se mantiene para compatibilidad
    // con las pruebas anteriores.
    public synchronized boolean intentarProducir(
            Object elemento) {

        if (elemento == null) {
            throw new IllegalArgumentException(
                    "El elemento no puede ser nulo");
        }

        if (estaLleno()) {
            return false;
        }

        almacenamiento[entrada] = elemento;
        entrada = (entrada + 1) % almacenamiento.length;
        cantidad++;

        return true;
    }

    // Operacion basica de consumo.
    public synchronized ResultadoConsumo intentarConsumir() {

        if (estaVacio()) {
            return new ResultadoConsumo(false, null);
        }

        Object elemento = almacenamiento[salida];
        almacenamiento[salida] = null;

        salida = (salida + 1) % almacenamiento.length;
        cantidad--;

        return new ResultadoConsumo(true, elemento);
    }

    public synchronized boolean estaLleno() {
        return cantidad == almacenamiento.length;
    }

    public synchronized boolean estaVacio() {
        return cantidad == 0;
    }

    public synchronized int getCantidad() {
        return cantidad;
    }

    public int getCapacidad() {
        return almacenamiento.length;
    }

    public String getIdBuffer() {
        return idBuffer;
    }

    public int getComputadorAnfitrionId() {
        return computadorAnfitrionId;
    }

    public SemaforoSimulado getMutex() {
        return mutex;
    }

    public SemaforoSimulado getLlenos() {
        return llenos;
    }

    public SemaforoSimulado getVacios() {
        return vacios;
    }

    public static class ResultadoConsumo {

        private final boolean exitoso;
        private final Object elemento;

        public ResultadoConsumo(
                boolean exitoso,
                Object elemento) {

            this.exitoso = exitoso;
            this.elemento = elemento;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public Object getElemento() {
            return elemento;
        }
    }
}
