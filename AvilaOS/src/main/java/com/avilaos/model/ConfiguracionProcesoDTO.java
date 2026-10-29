/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avilaos.config;

import com.avilaos.model.EstadoProceso;
import com.avilaos.model.TipoProceso;

/**
 *
 * @author Elohym Casais
 */

public class ConfiguracionProcesoDTO {
    private String id;
    private String nombre;
    private String computadorAsignadoId;
    private TipoProceso tipo;
    private int prioridad;
    private int memoriaRequerida;
    private int deadline;
    private int tiempoRestante;
    private String bufferObjetivoId; // Para productores/consumidores

    // Constructor vacío (necesario para librerías JSON)
    public ConfiguracionProcesoDTO() {}

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getComputadorAsignadoId() { return computadorAsignadoId; }
    public void setComputadorAsignadoId(String computadorAsignadoId) { this.computadorAsignadoId = computadorAsignadoId; }

    public TipoProceso getTipo() { return tipo; }
    public void setTipo(TipoProceso tipo) { this.tipo = tipo; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public int getMemoriaRequerida() { return memoriaRequerida; }
    public void setMemoriaRequerida(int memoriaRequerida) { this.memoriaRequerida = memoriaRequerida; }

    public int getDeadline() { return deadline; }
    public void setDeadline(int deadline) { this.deadline = deadline; }

    public int getTiempoRestante() { return tiempoRestante; }
    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; }

    public String getBufferObjetivoId() { return bufferObjetivoId; }
    public void setBufferObjetivoId(String bufferObjetivoId) { this.bufferObjetivoId = bufferObjetivoId; }
}
