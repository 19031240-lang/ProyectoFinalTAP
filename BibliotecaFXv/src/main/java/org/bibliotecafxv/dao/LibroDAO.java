package org.bibliotecafxv.dao;

import org.bibliotecafxv.connetion.ConexionBD;
import org.bibliotecafxv.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO implements GenericDAO<Libro> {

    private Connection conn;

    public LibroDAO() {
        conn = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void guardar(Libro libro) {

        String sql = """
                INSERT INTO libros
                (titulo, autor, categoria, cantidad)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getCategoria());
            ps.setInt(4, libro.getCantidad());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Libro> listar() {

        List<Libro> lista = new ArrayList<>();

        String sql = "SELECT * FROM libros";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getInt("cantidad")
                );

                lista.add(libro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public void actualizar(Libro libro) {

        String sql = """
                UPDATE libros
                SET titulo=?, autor=?, categoria=?, cantidad=?
                WHERE id=?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getCategoria());
            ps.setInt(4, libro.getCantidad());
            ps.setInt(5, libro.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(int id) {

        String sql = "DELETE FROM libros WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Libro> buscarPorTitulo(String titulo) {

        List<Libro> lista = new ArrayList<>();

        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + titulo + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponible")
                );

                lista.add(libro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}