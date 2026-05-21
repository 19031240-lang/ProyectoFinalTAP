package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Usuario.
 * Gestiona el registro, autenticación (login) y roles de acceso dentro de la biblioteca.
 */
public class UsuarioDAO implements GenericDAO<Usuario> {

    @Override
    public boolean guardar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, correo, password, rol) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRol());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("rol")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza la información de un usuario. Implementa lógica condicional de seguridad:
     * Si la contraseña viene vacía, omite actualizar ese campo en la base de datos
     * para no sobrescribir la contraseña actual (hash) del usuario.
     * * @param u Objeto Usuario con los datos a actualizar.
     * @return true si la actualización en BD fue exitosa.
     */
    @Override
    public boolean actualizar(Usuario u) {
        String sql;
        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            sql = "UPDATE usuarios SET nombre=?, correo=?, password=?, rol=? WHERE id_usuario=?";
            try (Connection con = ConexionBD.getInstancia().getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getNombre());
                ps.setString(2, u.getCorreo());
                ps.setString(3, u.getPassword());
                ps.setString(4, u.getRol());
                ps.setInt(5, u.getId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Error al actualizar usuario: " + e.getMessage());
                return false;
            }
        } else {
            sql = "UPDATE usuarios SET nombre=?, correo=?, rol=? WHERE id_usuario=?";
            try (Connection con = ConexionBD.getInstancia().getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, u.getNombre());
                ps.setString(2, u.getCorreo());
                ps.setString(3, u.getRol());
                ps.setInt(4, u.getId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                System.out.println("Error al actualizar usuario: " + e.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Calcula la cantidad total de usuarios registrados en el sistema.
     * @return El total de usuarios para alimentar métricas o vistas de Dashboard.
     */
    public int contarTotalUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuarios";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contando usuarios: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Busca a un usuario específico mediante su correo electrónico.
     * Este es el método central utilizado durante el proceso de Inicio de Sesión (Login).
     * * @param correo El correo ingresado por el usuario en pantalla.
     * @return El objeto Usuario completo si existe, o null si el correo no está registrado.
     */
    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre"),
                            rs.getString("correo"),
                            rs.getString("password"),
                            rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }
}