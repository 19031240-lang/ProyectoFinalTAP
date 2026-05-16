package org.bibliotecafxv.dao;

import java.util.List;

public interface GenericDAO<T> {
    boolean guardar(T obj);
    List<T> listar();
    boolean actualizar(T obj);
    boolean eliminar(int id);
}