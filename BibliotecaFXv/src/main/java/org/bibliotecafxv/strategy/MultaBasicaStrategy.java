package org.bibliotecafxv.strategy;

/**
 * Estrategia concreta para el cálculo de sanciones de tarifa regular.
 * Implementa el **Patrón Strategy** aplicando un costo fijo estándar por cada día de retraso.
 */
public class MultaBasicaStrategy implements MultaStrategy {

    /**
     * Aplica la fórmula estándar multiplicando los días de retraso por una tarifa base de 5.
     * @param diasRetraso Cantidad de días transcurridos tras el vencimiento.
     * @return Monto calculado de la multa (días de retraso multiplicado por 5).
     */
    @Override
    public double calcularMulta(int diasRetraso) {
        return diasRetraso * 5;
    }
}