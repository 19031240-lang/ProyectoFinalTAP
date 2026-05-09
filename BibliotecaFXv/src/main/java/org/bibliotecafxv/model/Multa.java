package org.bibliotecafxv.model;

public class Multa {

    private int id;
    private int prestamoId;
    private double monto;
    private boolean pagada;

    public Multa() {
    }

    public Multa(int id, int prestamoId,
                 double monto,
                 boolean pagada) {

        this.id = id;
        this.prestamoId = prestamoId;
        this.monto = monto;
        this.pagada = pagada;
    }

    public int getId() {
        return id;
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public double getMonto() {
        return monto;
    }

    public boolean isPagada() {
        return pagada;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setPagada(boolean pagada) {
        this.pagada = pagada;
    }
}