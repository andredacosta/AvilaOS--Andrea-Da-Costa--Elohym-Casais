package com.avilaos.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Elohym Casais
 */

public class ConfiguracionBufferDTO {
    private String idBuffer;
    private int capacidad;
    private String computadorAnfitrionId;

    public ConfiguracionBufferDTO() {}

    public String getIdBuffer() { return idBuffer; }
    public void setIdBuffer(String idBuffer) { this.idBuffer = idBuffer; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public String getComputadorAnfitrionId() { return computadorAnfitrionId; }
    public void setComputadorAnfitrionId(String computadorAnfitrionId) { this.computadorAnfitrionId = computadorAnfitrionId; }

}
