package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por correo");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return null;
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return null;
    }

    public void insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nombre, correo, password, rol) VALUES(?,?,?,?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Usuario registrado exitosamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public void actualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre=?, correo=?, password=?, rol=? WHERE id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol());
            ps.setInt(5, usuario.getId());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Usuario actualizado. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Usuario eliminado. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
                lista.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return lista;
    }

    public boolean validarCredenciales(String correo, String password) {
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND password = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, password);
            rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error al validar credenciales");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return false;
    }

    private void cerrarRecursos(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int contarTotalUsuarios() {
        String sql = "SELECT COUNT(*) AS total FROM usuarios";
        int total = 0;
        try (java.sql.Connection conn = org.bibliotecafxv.connetion.ConexionBD.getInstancia().getConexion();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Error al contar usuarios en la base de datos");
            e.printStackTrace();
        }
        return total;
    }
}