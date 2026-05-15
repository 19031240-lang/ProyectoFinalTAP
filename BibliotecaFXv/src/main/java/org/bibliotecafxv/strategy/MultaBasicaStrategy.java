package org.bibliotecafxv.strategy;

public class MultaBasicaStrategy implements MultaStrategy {

    @Override
    public double calcularMulta(int diasRetraso) {

        return diasRetraso * 5;
    }
}

