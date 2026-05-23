package org.bibliotecafxv.dao;

import org.bibliotecafxv.connetion.ConexionBD;
import org.bibliotecafxv.model.Autor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Autor.
 * Gestiona todas las transacciones SQL hacia la tabla 'autores'.
 */

public class AutorDAO implements GenericDAO<Autor> {

    @Override
    public List<Autor> listar() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT id_autor, nombre FROM autores ORDER BY nombre ASC";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            //Listar autores
            while (rs.next()) {
                System.out.println(rs.getInt("id_autor")+rs.getString("nombre"));
                lista.add(new Autor(rs.getInt("id_autor"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar autores: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean guardar(Autor autor) {
        String sql = "INSERT INTO autores (nombre) VALUES (?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Autor autor) {
        String sql = "UPDATE autores SET nombre=? WHERE id_autor=?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, autor.getNombre());
            ps.setInt(2, autor.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM autores WHERE id_autor=?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar autor: " + e.getMessage());
            return false;
        }
    }
}