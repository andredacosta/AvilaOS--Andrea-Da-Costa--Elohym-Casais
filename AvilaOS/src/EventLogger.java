/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Elohym Casais
 */

public class EventLogger {
    private static EventLogger instancia;
    private final StringBuilder logBuffer;

    private EventLogger() {
        this.logBuffer = new StringBuilder();
    }

    public static synchronized EventLogger getInstancia() {
        if (instancia == null) {
            instancia = new EventLogger();
        }
        return instancia;
    }

    public synchronized void registrarEvento(long ciclo, String mensaje) {
        String entrada = "[Ciclo " + ciclo + "] " + mensaje + "\n";
        logBuffer.append(entrada);
        System.out.print(entrada);
    }

    public synchronized String getLogCompleto() {
        return logBuffer.toString();
    }

    public synchronized void limpiar() {
        logBuffer.setLength(0);
    }
}
