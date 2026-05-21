package org.bibliotecafxv.strategy;

/**
 * Estrategia concreta para el cálculo de sanciones de tarifa elevada.
 * Implementa el **Patrón Strategy** aplicando un recargo mayor, ideal para libros de alta demanda,
 * material restringido o usuarios reincidentes.
 */
public class MultaPremiumStrategy implements MultaStrategy {

    /**
     * Aplica la fórmula de penalización severa multiplicando los días de retraso por una tarifa premium de 10.
     * @param diasRetraso Cantidad de días transcurridos tras el vencimiento.
     * @return Monto calculado de la multa (días de retraso multiplicado por 10).
     */
    @Override
    public double calcularMulta(int diasRetraso) {
        return diasRetraso * 10;
    }
}