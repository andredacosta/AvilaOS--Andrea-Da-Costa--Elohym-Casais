
package com.avilaos.structures;

public class Cola<T> {

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public boolean estaVacia() {
        return primero == null;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);

        if (estaVacia()) {
            primero = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }

        ultimo = nuevo;
        tamanio++;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }

        T dato = primero.getDato();
        primero = primero.getSiguiente();

        if (primero == null) {
            ultimo = null;
        }

        tamanio--;
        return dato;
    }

    public T verPrimero() {
        return estaVacia() ? null : primero.getDato();
    }
}
