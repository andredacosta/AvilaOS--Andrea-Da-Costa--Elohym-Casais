/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Elohym
 */

public class BufferBounded {
    private final String idBuffer;
    private final int capacidad;
    private final String computadorAnfitrionId;
    
    public String getidBuffer(){
        return idBuffer;
    }
    
    // Arreglo nativo de Java (Sin ArrayList ni colecciones)
    private final Object[] almacenamiento; 
    private int in = 0;
    private int out = 0;

    // Los 3 semáforos primitivos del algoritmo clásico (Stallings)
    private final Semaforo s; // Mutex (Exclusión mutua)
    private final Semaforo n; // Elementos llenos
    private final Semaforo e; // Espacios vacíos

    public BufferBounded(String idBuffer, int capacidad, String computadorAnfitrionId) {
        this.idBuffer = idBuffer;
        this.capacidad = capacidad;
        this.computadorAnfitrionId = computadorAnfitrionId;
        this.almacenamiento = new Object[capacidad];

        // Inicialización del algoritmo clásico
        this.s = new Semaforo(1, "mutex_" + idBuffer);
        this.n = new Semaforo(0, "lleno_" + idBuffer);
        this.e = new Semaforo(capacidad, "vacio_" + idBuffer);
    }

    // Método para producir un elemento
    public void anyadir(Object elemento, String computadorProcesoId, int latenciaRed) throws InterruptedException {
        // Latencia de red si el proceso viene de otro computador del clúster
        if (!computadorProcesoId.equals(this.computadorAnfitrionId) && latenciaRed > 0) {
            Thread.sleep(latenciaRed);
        }

        e.semWait(); // Espera espacio libre
        s.semWait(); // Entra a la Región Crítica

        // Inserción en arreglo circular
        almacenamiento[in] = elemento;
        in = (in + 1) % capacidad;

        s.semSignal(); // Sale de la Región Crítica
        n.semSignal(); // Incrementa elementos disponibles
    }

    // Método para consumir un elemento
    public Object extraer(String computadorProcesoId, int latenciaRed) throws InterruptedException {
        if (!computadorProcesoId.equals(this.computadorAnfitrionId) && latenciaRed > 0) {
            Thread.sleep(latenciaRed);
        }

        n.semWait(); // Espera que haya elementos
        s.semWait(); // Entra a la Región Crítica

        Object elemento = almacenamiento[out];
        almacenamiento[out] = null;
        out = (out + 1) % capacidad;

        s.semSignal(); // Sale de la Región Crítica
        e.semSignal(); // Incrementa espacios libres

        return elemento;
    }

    // Getters para consultar estado desde la GUI y Métricas
    public int getCantidadElementos() { return n.getValorActual(); }
    public int getCapacidad() { return capacidad; }
    public String getComputadorAnfitrionId() { return computadorAnfitrionId; }
    public Semaforo getMutex() { return s; }
    public Semaforo getLlenos() { return n; }
    public Semaforo getVacios() { return e; }
    
}
