package org.bibliotecafxv.observer;

public class AdminObserver implements Observer {

    @Override
    public void actualizar(String mensaje) {

        System.out.println("ADMIN: " + mensaje);
    }
}


