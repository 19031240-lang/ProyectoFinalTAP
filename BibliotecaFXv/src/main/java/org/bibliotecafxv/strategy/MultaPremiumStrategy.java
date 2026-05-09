package org.bibliotecafxv.strategy;

public class MultaPremiumStrategy implements MultaStrategy {

    @Override
    public double calcularMulta(int diasRetraso) {

        return diasRetraso * 5;
    }
}