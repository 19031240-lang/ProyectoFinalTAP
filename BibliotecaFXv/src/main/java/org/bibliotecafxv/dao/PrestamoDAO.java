package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.observer.AdminObserver;
import org.bibliotecafxv.observer.PrestamoNotifier;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO implements GenericDAO<Prestamo> {

    private final PrestamoNotifier notifier;

    public PrestamoDAO() {
        notifier = new PrestamoNotifier();
        notifier.agregarObserver(new AdminObserver());
    }

    @Override
    public void guardar(Prestamo prestamo) {
        String sql = """
                INSERT INTO prestamos
                (id_usuario, id_libro, fecha_prestamo, fecha_devolucion, estado)
                VALUES(?,?,?,?,?)
                """;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, prestamo.getUsuarioId());
            ps.setInt(2, prestamo.getLibroId());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setString(5, prestamo.getEstado());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Préstamo registrado exitosamente. Filas afectadas: " + filasAfectadas);

            notifier.notificar("Nuevo préstamo registrado: Usuario ID " +
                    prestamo.getUsuarioId() + " - Libro ID " + prestamo.getLibroId());

        } catch (SQLException e) {
            System.out.println("Error al guardar préstamo");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    @Override
    public List<Prestamo> listar() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos";

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }

        return lista;
    }

    public List<Prestamo> listarPorUsuario(int usuarioId) {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE id_usuario = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos por usuario");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return lista;
    }

    public List<Prestamo> listarPrestamosActivos() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE estado = 'ACTIVO'";

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos activos");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }

        return lista;
    }

    // =====================================================================
    // NUEVO MÉTODO: Trae los últimos 5 préstamos con el nombre del usuario y libro
    // =====================================================================
    public List<Prestamo> listarRecientes() {
        List<Prestamo> lista = new ArrayList<>();
        // Hacemos JOIN para obtener el nombre del usuario y el título del libro
        String sql = """
            SELECT p.*, u.nombre AS nombre_usuario, l.titulo AS titulo_libro 
            FROM prestamos p 
            JOIN usuarios u ON p.id_usuario = u.id 
            JOIN libros l ON p.id_libro = l.id 
            ORDER BY p.id DESC LIMIT 5
            """;

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Prestamo p = mapearPrestamo(rs);

                try {
                    p.setNombreUsuario(rs.getString("nombre_usuario"));
                    p.setTituloLibro(rs.getString("titulo_libro"));
                } catch (Exception ignore) {
                }

                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar préstamos recientes");
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void actualizar(Prestamo prestamo) {
        String sql = """
                UPDATE prestamos
                SET estado = ?
                WHERE id = ?
                """;

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setString(1, prestamo.getEstado());
            ps.setInt(2, prestamo.getId());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Préstamo actualizado. Filas afectadas: " + filasAfectadas);

            notifier.notificar("Préstamo ID " + prestamo.getId() +
                    " actualizado a estado: " + prestamo.getEstado());

        } catch (SQLException e) {
            System.out.println("Error al actualizar préstamo");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Préstamo eliminado. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al eliminar préstamo");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public boolean libroEstaPrestado(int libroId) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_libro = ? AND estado = 'ACTIVO'";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, libroId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar disponibilidad del libro");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return false;
    }

    public int contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) AS total FROM prestamos WHERE estado = 'ACTIVO'";
        int total = 0;
        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) total = rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public java.util.List<Integer> obtenerDiasConDevolucion(int mes, int anio) {
        java.util.List<Integer> dias = new java.util.ArrayList<>();
        String sql = "SELECT fecha_devolucion FROM prestamos WHERE estado = 'ACTIVO'";

        try (Connection conn = ConexionBD.getInstancia().getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                java.sql.Date fechaSql = rs.getDate("fecha_devolucion");
                if (fechaSql != null) {
                    LocalDate fecha = fechaSql.toLocalDate();
                    if (fecha.getMonthValue() == mes && fecha.getYear() == anio) {
                        dias.add(fecha.getDayOfMonth());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las fechas de los préstamos");
            e.printStackTrace();
        }

        return dias;
    }

    // Método auxiliar para evitar repetir código al leer un Préstamo de la BD
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setId(rs.getInt("id"));
        p.setUsuarioId(rs.getInt("id_usuario"));
        p.setLibroId(rs.getInt("id_libro"));

        Date fechaPrestamo = rs.getDate("fecha_prestamo");
        if (fechaPrestamo != null) {
            p.setFechaPrestamo(fechaPrestamo.toLocalDate());
        }

        Date fechaDevolucion = rs.getDate("fecha_devolucion");
        if (fechaDevolucion != null) {
            p.setFechaDevolucion(fechaDevolucion.toLocalDate());
        }

        p.setEstado(rs.getString("estado"));
        return p;
    }

    private void cerrarRecursos(ResultSet rs, Statement st) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}