package org.bibliotecafxv.dao;

import org.bibliotecafxv.connetion.ConexionBD;
import org.bibliotecafxv.model.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Libro.
 * Contiene la lógica de negocio para interactuar con la tabla 'libros',
 * incluyendo consultas complejas que relacionan autores y categorías.
 */
public class LibroDAO implements GenericDAO<Libro> {

    /**
     * Recupera todos los libros registrados, realizando uniones (LEFT JOIN)
     * con las tablas de autores y categorías para obtener los nombres reales
     * en lugar de solo mostrar los identificadores numéricos.
     * * @return Lista completa de libros con los datos de autor y categoría integrados.
     */
    @Override
    public List<Libro> listar() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT l.id_libro, l.titulo, l.autor_id, l.categoria_id, a.nombre AS autor_nombre, " +
                "c.nombre AS categoria_nombre, l.disponible, l.descripcion, l.portada " +
                "FROM libros l " +
                "LEFT JOIN autores a ON l.autor_id = a.id_autor " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id_categoria";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id_libro"),
                        rs.getString("titulo"),
                        rs.getInt("autor_id"),
                        rs.getInt("categoria_id"),
                        rs.getString("autor_nombre") != null ? rs.getString("autor_nombre") : "Desconocido",
                        rs.getString("categoria_nombre") != null ? rs.getString("categoria_nombre") : "Sin categoría",
                        rs.getBoolean("disponible"),
                        rs.getString("descripcion"),
                        rs.getString("portada")
                );
                lista.add(libro);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean guardar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor_id, categoria_id, disponible, descripcion, portada) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAutorId());
            ps.setInt(3, libro.getCategoriaId());
            ps.setBoolean(4, libro.isDisponible());
            ps.setString(5, libro.getDescripcion());
            ps.setString(6, libro.getPortada());
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor_id=?, categoria_id=?, disponible=?, descripcion=?, portada=? WHERE id_libro=?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getAutorId());
            ps.setInt(3, libro.getCategoriaId());
            ps.setBoolean(4, libro.isDisponible());
            ps.setString(5, libro.getDescripcion());
            ps.setString(6, libro.getPortada());
            ps.setInt(7, libro.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM libros WHERE id_libro=?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un libro específico utilizando su ID.
     * Incorpora cruce de tablas (LEFT JOIN) para recuperar la información completa
     * del libro, incluyendo nombres del autor y la categoría.
     * * @param id El identificador único del libro a buscar.
     * @return Objeto Libro con la información completa si se encuentra, o null en caso contrario.
     */
    public Libro buscarPorId(int id) {
        String sql = "SELECT l.id_libro, l.titulo, l.autor_id, l.categoria_id, a.nombre AS autor_nombre, " +
                "c.nombre AS categoria_nombre, l.disponible, l.descripcion, l.portada " +
                "FROM libros l " +
                "LEFT JOIN autores a ON l.autor_id = a.id_autor " +
                "LEFT JOIN categorias c ON l.categoria_id = c.id_categoria WHERE l.id_libro=?";
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Libro(
                            rs.getInt("id_libro"),
                            rs.getString("titulo"),
                            rs.getInt("autor_id"),
                            rs.getInt("categoria_id"),
                            rs.getString("autor_nombre") != null ? rs.getString("autor_nombre") : "Desconocido",
                            rs.getString("categoria_nombre") != null ? rs.getString("categoria_nombre") : "Sin categoría",
                            rs.getBoolean("disponible"),
                            rs.getString("descripcion"),
                            rs.getString("portada")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar libro por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Calcula la cantidad total de libros registrados en la base de datos.
     * Utilizado principalmente para las estadísticas del Dashboard.
     * * @return Un número entero que representa el total de libros, o 0 si ocurre un error.
     */
    public int contarTotalLibros() {
        String sql = "SELECT COUNT(*) FROM libros";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar libros: " + e.getMessage());
        }
        return 0;
    }
}