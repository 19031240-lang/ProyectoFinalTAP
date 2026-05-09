package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Usuario;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    private final Connection conn;

    public UsuarioDAO() {
        conn = ConexionBD.getInstancia().getConexion();
    }

    public Usuario buscarPorCorreo(String correo) {

        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try {

            PreparedStatement ps = conn.prepareStatement(sql);

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

        String sql =
                "INSERT INTO usuarios(nombre, correo, password, rol) VALUES(?,?,?,?)";

        try {

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getCorreo());
            ps.setString(3, usuario.getPassword());
            ps.setString(4, usuario.getRol());

            ps.executeUpdate();

            System.out.println("Usuario registrado");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}