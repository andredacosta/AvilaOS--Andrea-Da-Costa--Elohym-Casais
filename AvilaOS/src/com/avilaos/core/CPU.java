
package com.avilaos.core;

import com.avilaos.model.PCB;
import com.avilaos.model.Proceso;
import com.avilaos.model.EstadoProceso;
import com.avilaos.model.ModoCPU;

public class CPU {

    private Proceso procesoActual;
    private ModoCPU modo;

    private int ciclosEjecutados;
    private int ciclosOcupados;

    public CPU() {
        this.procesoActual = null;
        this.modo = ModoCPU.SISTEMA_OPERATIVO;
        this.ciclosEjecutados = 0;
        this.ciclosOcupados = 0;
    }

    public Proceso getProcesoActual() {
        return procesoActual;
    }

    public ModoCPU getModo() {
        return modo;
    }

    public boolean estaLibre() {
        return procesoActual == null;
    }

    public void asignarProceso(Proceso proceso) {
        if (proceso == null) {
            throw new IllegalArgumentException(
                "El proceso no puede ser nulo");
        }

        if (!estaLibre()) {
            throw new IllegalStateException(
                "La CPU ya tiene un proceso asignado");
        }

        PCB pcb = proceso.getPcb();

        if (pcb.getEstado() != EstadoProceso.LISTO) {
            throw new IllegalStateException(
                "El proceso debe estar LISTO");
        }

        procesoActual = proceso;
        pcb.setEstado(EstadoProceso.EJECUCION);
        modo = ModoCPU.USUARIO;
    }

    public void ejecutarCiclo() {
        ciclosEjecutados++;

        if (procesoActual == null) {
            modo = ModoCPU.SISTEMA_OPERATIVO;
            return;
        }

        PCB pcb = procesoActual.getPcb();

        if (pcb.getEstado() != EstadoProceso.EJECUCION
                || pcb.getDeadline() <= 0) {
            if (pcb.getDeadline() <= 0) {
                procesoActual.finalizar();
            }
            liberarCPU();
            return;
        }

        modo = ModoCPU.USUARIO;
        ciclosOcupados++;
        procesoActual.ejecutarCiclo();

        if (procesoActual.estaTerminado()) {
            liberarCPU();
        }
    }

    public Proceso desalojarProceso() {
        if (procesoActual == null) {
            return null;
        }

        Proceso desalojado = procesoActual;

        if (!desalojado.estaTerminado()) {
            desalojado.getPcb().setEstado(
                EstadoProceso.LISTO);
        }

        liberarCPU();
        return desalojado;
    }

    public void liberarCPU() {
        procesoActual = null;
        modo = ModoCPU.SISTEMA_OPERATIVO;
    }

    public int getCiclosEjecutados() {
        return ciclosEjecutados;
    }

    public int getCiclosOcupados() {
        return ciclosOcupados;
    }

    public double getUtilizacion() {
        if (ciclosEjecutados == 0) {
            return 0;
        }

        return (ciclosOcupados * 100.0)
                / ciclosEjecutados;
    }
}
