/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Elohym
 */

public class Semaforo {
    private int valor;
    private final String nombre;
    
    public Semaforo(int valorInicial, String nombre) {
        this.valor = valorInicial;
        this.nombre = nombre;
    }

    // Operación semWait (Down / P)
    public synchronized void semWait() throws InterruptedException {
        while (this.valor <= 0) {
            // El hilo se bloquea hasta que haya un permiso disponible
            this.wait();
        }
        this.valor--;
    }

    // Operación semSignal (Up / V)
    public synchronized void semSignal() {
        this.valor++;
        // Despierta a los hilos que están esperando en este semáforo
        this.notifyAll();
    }

    public synchronized int getValorActual() {
        return this.valor;
    }

    public String getNombre() {
        return nombre;
    }
    
}
