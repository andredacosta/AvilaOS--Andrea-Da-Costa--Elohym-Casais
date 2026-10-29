
package com.avilaos.structures;

public class Nodo<T> {

    private final T dato;
    private Nodo<T> siguiente;

    public Nodo(T dato) {
        if (dato == null) {
            throw new IllegalArgumentException(
                "El dato no puede ser nulo");
        }

        this.dato = dato;
    }

    public T getDato() {
        return dato;
    }

    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
