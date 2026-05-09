package org.bibliotecafxv.dao;

import java.util.List;

public interface GenericDAO<T> {

    void insertar(T obj);

    void actualizar(T obj);

    void eliminar(int id);

    List<T> listar();

    T buscarPorId(int id);
}