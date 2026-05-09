package org.bibliotecafxv.observer;

public interface Subject {

    void agregarObserver(Observer observer);

    void eliminarObserver(Observer observer);

    void notificar(String mensaje);
}
