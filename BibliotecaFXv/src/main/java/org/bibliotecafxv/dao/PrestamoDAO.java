package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.connetion.ConexionBD;
import org.bibliotecafxv.observer.PrestamoNotifier;
import org.bibliotecafxv.observer.AdminObserver;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Prestamo.
 * Es el núcleo transaccional del sistema, ya que vincula a los usuarios con los libros,
 * gestiona fechas, estados y alimenta los indicadores del Dashboard.
 * * Además, actúa como el disparador de eventos para el Patrón Observer.
 */
public class PrestamoDAO implements GenericDAO<Prestamo> {

    private final PrestamoNotifier notifier;

    /**
     * Constructor de PrestamoDAO.
     * Aquí inicializamos el notificador y registramos (suscribimos) al Administrador
     * para que escuche de forma automática todos los eventos de préstamos.
     */
    public PrestamoDAO() {
        this.notifier = new PrestamoNotifier();
        this.notifier.agregarObserver(new AdminObserver());
    }

    @Override
    public boolean guardar(Prestamo p) {
        String sql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, fecha_devolucion, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUsuario());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFechaPrestamo());
            ps.setDate(4, p.getFechaDevolucion());
            ps.setString(5, p.getEstado());

            boolean exito = ps.executeUpdate() > 0;
            if (exito) {
                notifier.notificar("¡ALERTA SOLICITUD! El usuario ID [" + p.getIdUsuario() +
                        "] ha solicitado el libro ID [" + p.getIdLibro() + "].");
            }

            return exito;
        } catch (SQLException e) {
            System.out.println("Error al guardar préstamo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera todos los préstamos realizando un cruce (INNER JOIN) con las tablas
     * de usuarios y libros para obtener nombres y títulos legibles.
     * * @return Lista completa de préstamos con información relacional integrada.
     */
    @Override
    public List<Prestamo> listar() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, l.titulo AS tituloLibro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "INNER JOIN libros l ON p.id_libro = l.id_libro";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Prestamo p = new Prestamo(
                        rs.getInt("id_prestamo"),
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

    @Override
    public boolean actualizar(Prestamo p) {
        String sql = "UPDATE prestamos SET id_usuario = ?, id_libro = ?, fecha_devolucion = ?, estado = ? WHERE id_prestamo = ?";
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

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM prestamos WHERE id_prestamo = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }

    public Prestamo buscarPorId(int id) {
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, l.titulo AS tituloLibro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "INNER JOIN libros l ON p.id_libro = l.id_libro WHERE p.id_prestamo = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prestamo p = new Prestamo(
                            rs.getInt("id_prestamo"),
                            rs.getInt("id_usuario"),
                            rs.getInt("id_libro"),
                            rs.getDate("fecha_prestamo"),
                            rs.getDate("fecha_devolucion"),
                            rs.getString("estado")
                    );
                    p.setNombreUsuario(rs.getString("nombreUsuario"));
                    p.setTituloLibro(rs.getString("tituloLibro"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar préstamo por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cambia el estado de un préstamo de 'ACTIVO' a 'DEVUELTO', finalizando la transacción.
     * * @param idPrestamo El identificador del préstamo que se está devolviendo.
     * @return true si el estado se actualizó correctamente.
     */
    public boolean marcarComoDevuelto(int idPrestamo) {
        String sql = "UPDATE prestamos SET estado = 'DEVUELTO' WHERE id_prestamo = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);

            boolean exito = ps.executeUpdate() > 0;
            if (exito) {
                notifier.notificar("¡SISTEMA! El préstamo con ID [" + idPrestamo + "] ha sido DEVUELTO correctamente.");
            }

            return exito;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del préstamo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calcula la cantidad de libros que se encuentran prestados actualmente.
     * Utilizado para métricas en tiempo real.
     * * @return Número de préstamos cuyo estado es 'ACTIVO'.
     */
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

    /**
     * Obtiene una lista rápida de las 5 transacciones de préstamos más recientes,
     * ordenadas descendentemente por ID. Ideal para un widget de actividad.
     * * @return Lista con los últimos 5 préstamos registrados.
     */
    public List<Prestamo> listarRecientes() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.*, u.nombre AS nombreUsuario, l.titulo AS tituloLibro " +
                "FROM prestamos p " +
                "INNER JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "INNER JOIN libros l ON p.id_libro = l.id_libro " +
                "ORDER BY p.id_prestamo DESC LIMIT 5";
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

    /**
     * Extrae los días específicos del mes actual en los que hay devoluciones programadas.
     * Utilizado para pintar o marcar eventos en un componente de calendario visual.
     * * @param mes El mes numérico a consultar (1-12).
     * @param anio El año a consultar (ej. 2026).
     * @return Lista de enteros que representan los días del mes con vencimientos activos.
     */
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