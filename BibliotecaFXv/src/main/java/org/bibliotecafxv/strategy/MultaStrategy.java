package org.bibliotecafxv.strategy;

/**
 * Interfaz base que define el contrato para el **Patrón de Diseño Strategy**.
 * Centraliza la lógica de las penalizaciones económicas, permitiendo cambiar
 * dinámicamente el algoritmo de cálculo en tiempo de ejecución según el tipo de préstamo o usuario.
 */
public interface MultaStrategy {

    /**
     * Calcula el monto total de la multa basándose en el tiempo de demora.
     * @param diasRetraso Cantidad de días naturales que el usuario excedió la fecha de devolución.
     * @return El importe económico total acumulado por la penalización.
     */
    double calcularMulta(int diasRetraso);
}