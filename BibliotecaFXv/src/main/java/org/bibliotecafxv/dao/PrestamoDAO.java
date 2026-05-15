package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {
    public List<Prestamo> listar() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, l.titulo AS tituloLibro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo p = new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_libro"),
                        rs.getDate("fecha_prestamo"),
                        rs.getDate("fecha_devolucion"),
                        rs.getString("estado")
                );
                p.setNombreUsuario(rs.getString("nombreUsuario"));
                p.setTituloLibro(rs.getString("tituloLibro"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }
        return lista;
    }

    public boolean guardar(Prestamo p) {
        String sql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getIdUsuario());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFechaPrestamo());
            ps.setDate(4, p.getFechaDevolucion());
            ps.setString(5, p.getEstado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar préstamo: " + e.getMessage());
            return false;
        }
    }
// ... (mismos imports de antes)

    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Prestamo p) {
        String sql = "UPDATE prestamos SET id_usuario = ?, id_libro = ?, fecha_devolucion = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUsuario());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFechaDevolucion());
            ps.setString(4, p.getEstado());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }
    public boolean marcarComoDevuelto(int idPrestamo) {
        String sql = "UPDATE prestamos SET estado = 'DEVUELTO' WHERE id = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del préstamo: " + e.getMessage());
            return false;
        }
    }

    // --- MÉTODOS ESPECIALES PARA QUE EL DASHBOARD FUNCIONE ---

    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'ACTIVO'";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar préstamos activos: " + e.getMessage());
        }
        return 0;
    }

    public List<Prestamo> listarRecientes() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, l.titulo AS tituloLibro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id " +
                "INNER JOIN libros l ON p.id_libro = l.id " +
                "ORDER BY p.id DESC LIMIT 5";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setNombreUsuario(rs.getString("nombreUsuario"));
                p.setTituloLibro(rs.getString("tituloLibro"));
                p.setFechaDevolucion(rs.getDate("fecha_devolucion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar préstamos recientes: " + e.getMessage());
        }
        return lista;
    }

    public List<Integer> obtenerDiasConDevolucion(int mes, int anio) {
        List<Integer> dias = new ArrayList<>();
        String sql = "SELECT DAY(fecha_devolucion) FROM prestamos " +
                "WHERE MONTH(fecha_devolucion) = ? AND YEAR(fecha_devolucion) = ? AND estado = 'ACTIVO'";

        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, anio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dias.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener días del calendario: " + e.getMessage());
        }
        return dias;
    }
}