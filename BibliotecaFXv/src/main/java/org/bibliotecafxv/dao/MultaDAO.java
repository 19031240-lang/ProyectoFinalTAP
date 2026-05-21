package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Multa;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Acceso a Datos (DAO) para la entidad Multa.
 * Gestiona las penalizaciones económicas generadas por retrasos en los préstamos.
 */
public class MultaDAO implements GenericDAO<Multa> {

    @Override
    public boolean guardar(Multa multa) {
        String sql = "INSERT INTO multas(prestamo_id, monto, pagada) VALUES(?,?,?)";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, multa.getPrestamoId());
            ps.setDouble(2, multa.getMonto());
            ps.setBoolean(3, multa.isPagada());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar multa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Multa> listar() {
        List<Multa> lista = new ArrayList<>();
        String sql = "SELECT * FROM multas";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Multa m = new Multa();
                m.setId(rs.getInt("id_multa"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setMonto(rs.getDouble("monto"));
                m.setPagada(rs.getBoolean("pagada"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar multas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean actualizar(Multa multa) {
        String sql = "UPDATE multas SET prestamo_id=?, monto=?, pagada=? WHERE id_multa=?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, multa.getPrestamoId());
            ps.setDouble(2, multa.getMonto());
            ps.setBoolean(3, multa.isPagada());
            ps.setInt(4, multa.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar multa: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM multas WHERE id_multa=?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar multa: " + e.getMessage());
            return false;
        }
    }

    public Multa buscarPorId(int id) {
        String sql = "SELECT * FROM multas WHERE id_multa = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Multa m = new Multa();
                    m.setId(rs.getInt("id_multa"));
                    m.setPrestamoId(rs.getInt("prestamo_id"));
                    m.setMonto(rs.getDouble("monto"));
                    m.setPagada(rs.getBoolean("pagada"));
                    return m;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar multa por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Recupera exclusivamente las multas que aún no han sido saldadas por los usuarios.
     * Útil para generar reportes de deudores.
     * * @return Lista de multas cuyo estado 'pagada' es false.
     */
    public List<Multa> listarMultasPendientes() {
        List<Multa> lista = new ArrayList<>();
        String sql = "SELECT * FROM multas WHERE pagada = false";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Multa m = new Multa();
                m.setId(rs.getInt("id_multa"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setMonto(rs.getDouble("monto"));
                m.setPagada(rs.getBoolean("pagada"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar multas pendientes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza el estado lógico de una multa específica indicando que la deuda ha sido saldada.
     * * @param id El identificador único de la multa a pagar.
     */
    public void marcarComoPagada(int id) {
        String sql = "UPDATE multas SET pagada = true WHERE id_multa = ?";
        try (Connection con = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al marcar multa como pagada: " + e.getMessage());
        }
    }
}