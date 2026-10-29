
package com.avilaos.core;

import com.avilaos.sync.BufferSimulado;

public class PruebaBufferSimulado {

    public static void main(String[] args) {

        BufferSimulado buffer =
                new BufferSimulado("BUFFER-1", 2, 1);

        System.out.println(
                "========== PRUEBA BUFFER ==========");

        boolean primero = buffer.intentarProducir("A");
        boolean segundo = buffer.intentarProducir("B");
        boolean tercero = buffer.intentarProducir("C");

        System.out.println("Producir A: " + primero);
        System.out.println("Producir B: " + segundo);
        System.out.println(
                "Producir C con buffer lleno: " + tercero);

        BufferSimulado.ResultadoConsumo r1 =
                buffer.intentarConsumir();

        BufferSimulado.ResultadoConsumo r2 =
                buffer.intentarConsumir();

        BufferSimulado.ResultadoConsumo r3 =
                buffer.intentarConsumir();

        System.out.println(
                "Consumir 1: " + r1.getElemento());
        System.out.println(
                "Consumir 2: " + r2.getElemento());
        System.out.println(
                "Consumir con buffer vacio: "
                + r3.isExitoso());

        System.out.println(
                "Cantidad final: " + buffer.getCantidad());

        boolean correcto =
                primero
                && segundo
                && !tercero
                && r1.isExitoso()
                && "A".equals(r1.getElemento())
                && r2.isExitoso()
                && "B".equals(r2.getElemento())
                && !r3.isExitoso()
                && buffer.getCantidad() == 0;

        System.out.println(
                "PRUEBA BUFFER: "
                + (correcto ? "PASS" : "FAIL"));
    }
}
