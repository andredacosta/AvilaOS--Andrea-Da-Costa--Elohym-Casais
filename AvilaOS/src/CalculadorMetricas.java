/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Elohym Casais
 */

public class CalculadorMetricas {
    private int procesosCompletadosTotales;
    private int procesosDeadlinesFallados;
    private long ciclosTotalesSimulacion;
    private long ciclosCPUEjecutando;

    public CalculadorMetricas() {
        this.procesosCompletadosTotales = 0;
        this.procesosDeadlinesFallados = 0;
        this.ciclosTotalesSimulacion = 0;
        this.ciclosCPUEjecutando = 0;
    }

    public void registrarCicloSimulacion(boolean cpuOcupada) {
        ciclosTotalesSimulacion++;
        if (cpuOcupada) {
            ciclosCPUEjecutando++;
        }
    }

    public void registrarProcesoTerminado(boolean cumplioDeadline) {
        procesosCompletadosTotales++;
        if (!cumplioDeadline) {
            procesosDeadlinesFallados++;
        }
    }

    public double calcularThroughput() {
        if (ciclosTotalesSimulacion == 0) return 0.0;
        return (double) procesosCompletadosTotales / ciclosTotalesSimulacion;
    }

    public double calcularPorcentajeUtilizacionCPU() {
        if (ciclosTotalesSimulacion == 0) return 0.0;
        return ((double) ciclosCPUEjecutando / ciclosTotalesSimulacion) * 100.0;
    }

    public double calcularTasaExitoDeadlines() {
        if (procesosCompletadosTotales == 0) return 100.0;
        int aTiempo = procesosCompletadosTotales - procesosDeadlinesFallados;
        return ((double) aTiempo / procesosCompletadosTotales) * 100.0;
    }
}
