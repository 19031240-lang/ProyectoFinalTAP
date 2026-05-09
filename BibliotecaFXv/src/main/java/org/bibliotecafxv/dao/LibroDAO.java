package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Libro;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO implements GenericDAO<Libro> {

    private final Connection conn;

    public LibroDAO() {
        conn = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(Libro libro) {

        String sql = "INSERT INTO libros(titulo,autor,categoria,cantidad) VALUES(?,?,?,?)";

        try {

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getCategoria());
            ps.setInt(4, libro.getCantidad());

            ps.executeUpdate();

            System.out.println("Libro insertado");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Libro> listar() {

        List<Libro> lista = new ArrayList<>();

        String sql = "SELECT * FROM libros";

        try {

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getInt("stock")
                );

                lista.add(libro);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public Libro buscarPorId(int id) {
        return null;
    }

    @Override
    public void actualizar(Libro obj) {

    }

    @Override
    public void eliminar(int id) {

    }
}