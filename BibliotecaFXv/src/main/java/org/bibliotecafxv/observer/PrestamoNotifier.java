package org.bibliotecafxv.observer;

import java.util.ArrayList;
import java.util.List;

public class PrestamoNotifier implements Subject {

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void agregarObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void eliminarObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notificar(String mensaje) {

        for (Observer observer : observers) {
            observer.actualizar(mensaje);
        }
    }
}


