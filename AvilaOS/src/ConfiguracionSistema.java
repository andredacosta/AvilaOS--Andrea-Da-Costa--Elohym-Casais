/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avilaos.config;

import com.avilaos.config.ConfiguracionBufferDTO;
import com.avilaos.config.ConfiguracionBufferDTO;
import com.avilaos.config.ConfiguracionBufferDTO;

/**
 *
 * @author Elohym Casais
 */
public class ConfiguracionSistema {
   private int duracionCicloMs;
   private int latenciaRedCiclos;
    
    private ConfiguracionComputadorDTO[] computadores;
    private ConfiguracionBufferDTO[] buffers;
    private ConfiguracionProcesoDTO[] procesosIniciales;

    public ConfiguracionSistema() {}

    public int getDuracionCicloMs() { return duracionCicloMs; }
    public void setDuracionCicloMs(int duracionCicloMs) { this.duracionCicloMs = duracionCicloMs; }

    public int getLatenciaRedCiclos() { return latenciaRedCiclos; }
    public void setLatenciaRedCiclos(int latenciaRedCiclos) { this.latenciaRedCiclos = latenciaRedCiclos; }

    public ConfiguracionComputadorDTO[] getComputadores() { return computadores; }
    public void setComputadores(ConfiguracionComputadorDTO[] computadores) { this.computadores = computadores; }

    public ConfiguracionBufferDTO[] getBuffers() { return buffers; }
    public void setBuffers(ConfiguracionBufferDTO[] buffers) { this.buffers = buffers; }

    public ConfiguracionProcesoDTO[] getProcesosIniciales() { return procesosIniciales; }
    public void setProcesosIniciales(ConfiguracionProcesoDTO[] procesosIniciales) { this.procesosIniciales = procesosIniciales; } 
}
