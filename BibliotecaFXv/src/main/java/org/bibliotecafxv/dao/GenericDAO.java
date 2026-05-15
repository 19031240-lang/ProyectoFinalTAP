package org.bibliotecafxv.dao;

import java.util.List;

public interface GenericDAO<T> {
    void guardar(T obj);
    List<T> listar();
    void actualizar(T obj);
    void eliminar(int id);
}