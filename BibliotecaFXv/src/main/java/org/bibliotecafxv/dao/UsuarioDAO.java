package org.bibliotecafxv.dao;


import org.bibliotecafxv.connetion.ConexionBD;
import org.bibliotecafxv.model.Usuario;

import java.sql.*;


public class UsuarioDAO {

    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios(nombre, correo, password, rol) VALUES(?,?,?,?)";

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}