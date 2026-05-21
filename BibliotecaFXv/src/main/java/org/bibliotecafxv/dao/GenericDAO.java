package org.bibliotecafxv.dao;

import java.util.List;

/**
 * Interfaz genérica que define el contrato estándar para las operaciones CRUD
 * (Crear, Leer, Actualizar, Eliminar) en la base de datos.
 * * @param <T> El tipo de entidad o modelo que manejará el DAO (ej. Libro, Autor, Usuario).
 */
public interface GenericDAO<T> {

    /**
     * Guarda un nuevo registro en la base de datos.
     * @param obj El objeto con los datos a guardar.
     * @return true si la inserción fue exitosa, false si ocurrió un error.
     */
    boolean guardar(T obj);

    /**
     * Recupera todos los registros de la tabla correspondiente.
     * @return Una lista con todos los objetos encontrados.
     */
    List<T> listar();

    /**
     * Actualiza los datos de un registro existente.
     * @param obj El objeto con los datos actualizados (debe incluir el ID original).
     * @return true si la actualización fue exitosa, false si ocurrió un error.
     */
    boolean actualizar(T obj);

    /**
     * Elimina un registro de la base de datos basándose en su identificador.
     * @param id El identificador único del registro a eliminar.
     * @return true si se eliminó correctamente, false si ocurrió un error.
     */
    boolean eliminar(int id);
}