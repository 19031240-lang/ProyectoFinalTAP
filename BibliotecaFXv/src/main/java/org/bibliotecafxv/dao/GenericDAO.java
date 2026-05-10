package org.bibliotecafxv.dao;

import java.util.List;

public interface GenericDAO<T> {

    void guardar(T t);

    List<T> listar();

    void actualizar(T t);

    void eliminar(int id);
}