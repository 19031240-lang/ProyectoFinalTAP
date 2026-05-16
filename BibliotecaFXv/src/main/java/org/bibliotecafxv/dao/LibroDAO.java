package org.bibliotecafxv.dao;

import org.bibliotecafxv.connetion.ConexionBD;
import org.bibliotecafxv.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO implements GenericDAO<Libro> {

    @Override
    public boolean guardar(Libro libro) {
        String sql = """
                INSERT INTO libros
                (titulo, autor, categoria, disponible, descripcion, portada)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getCategoria());
            ps.setBoolean(4, libro.isDisponible());
            ps.setString(5, libro.getDescripcion());
            ps.setString(6, libro.getPortada());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Libro guardado exitosamente. Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al guardar el libro");
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, null);
        }
    }

    @Override
    public List<Libro> listar() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponible"),
                        rs.getString("descripcion"),
                        rs.getString("portada")
                );
                lista.add(libro);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar libros");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st, null);
        }

        return lista;
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = """
                UPDATE libros
                SET titulo=?,
                    autor=?,
                    categoria=?,
                    disponible=?,
                    descripcion=?,
                    portada=?
                WHERE id=?
                """;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getCategoria());
            ps.setBoolean(4, libro.isDisponible());
            ps.setString(5, libro.getDescripcion());
            ps.setString(6, libro.getPortada());
            ps.setInt(7, libro.getId());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Libro actualizado. Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el libro");
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, null);
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Libro eliminado. Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el libro");
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(null, ps, null);
        }
    }

    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE titulo LIKE ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + titulo + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponible"),
                        rs.getString("descripcion"),
                        rs.getString("portada")
                );
                lista.add(libro);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar libros por título");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, null);
        }

        return lista;
    }

    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE autor LIKE ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + autor + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("categoria"),
                        rs.getBoolean("disponible"),
                        rs.getString("descripcion"),
                        rs.getString("portada")
                );
                lista.add(libro);
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar libros por autor");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps, null);
        }

        return lista;
    }

    private void cerrarRecursos(ResultSet rs, Statement st, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int contarTotalLibros() {
        String sql = "SELECT COUNT(*) AS total FROM libros";
        int total = 0;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error al contar libros");
            e.printStackTrace();
        }
        return total;
    }

    public void actualizarEstadoDisponible(int idLibro, boolean disponible) {
        String sql = "UPDATE libros SET disponible = ? WHERE id = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, idLibro);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
}