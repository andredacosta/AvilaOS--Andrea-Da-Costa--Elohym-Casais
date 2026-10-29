/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.avilaos.config;

import com.avilaos.model.PolititcaPlanificacion;

/**
 *
 * @author Elohym Casais
 */
public class ConfiguracionComputadorDTO {
    public class ConfiguracionComputadorDTO {
    private String id;
    private int capacidadRAM;
    private PoliticaPlanificacion politicaInicial;
    private int quantumInicial;

    public ConfiguracionComputadorDTO() {}

    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getCapacidadRAM() { return capacidadRAM; }
    public void setCapacidadRAM(int capacidadRAM) { this.capacidadRAM = capacidadRAM; }

    public PoliticaPlanificacion getPoliticaInicial() { return politicaInicial; }
    public void setPoliticaInicial(PoliticaPlanificacion politicaInicial) { this.politicaInicial = politicaInicial; }

    public int getQuantumInicial() { return quantumInicial; }
    public void setQuantumInicial(int quantumInicial) { this.quantumInicial = quantumInicial; }
}
}
