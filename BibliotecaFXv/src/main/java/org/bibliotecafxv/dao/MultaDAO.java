package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Multa;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MultaDAO implements GenericDAO<Multa> {

    @Override
    public void guardar(Multa multa) {
        String sql = "INSERT INTO multas(prestamo_id, monto, pagada) VALUES(?,?,?)";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, multa.getPrestamoId());
            ps.setDouble(2, multa.getMonto());
            ps.setBoolean(3, multa.isPagada());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Multa registrada exitosamente. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al guardar multa");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    @Override
    public List<Multa> listar() {
        List<Multa> lista = new ArrayList<>();
        String sql = "SELECT * FROM multas";

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Multa m = new Multa();
                m.setId(rs.getInt("id"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setMonto(rs.getDouble("monto"));
                m.setPagada(rs.getBoolean("pagada"));
                lista.add(m);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar multas");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }

        return lista;
    }

    @Override
    public void actualizar(Multa multa) {
        String sql = "UPDATE multas SET prestamo_id=?, monto=?, pagada=? WHERE id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);

            ps.setInt(1, multa.getPrestamoId());
            ps.setDouble(2, multa.getMonto());
            ps.setBoolean(3, multa.isPagada());
            ps.setInt(4, multa.getId());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Multa actualizada. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al actualizar multa");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM multas WHERE id=?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Multa eliminada. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al eliminar multa");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
    }

    public Multa buscarPorId(int id) {
        String sql = "SELECT * FROM multas WHERE id = ?";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                Multa m = new Multa();
                m.setId(rs.getInt("id"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setMonto(rs.getDouble("monto"));
                m.setPagada(rs.getBoolean("pagada"));
                return m;
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar multa por ID");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, ps);
        }

        return null;
    }

    public List<Multa> listarMultasPendientes() {
        List<Multa> lista = new ArrayList<>();
        String sql = "SELECT * FROM multas WHERE pagada = false";

        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            while (rs.next()) {
                Multa m = new Multa();
                m.setId(rs.getInt("id"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setMonto(rs.getDouble("monto"));
                m.setPagada(rs.getBoolean("pagada"));
                lista.add(m);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar multas pendientes");
            e.printStackTrace();
        } finally {
            cerrarRecursos(rs, st);
        }

        return lista;
    }

    public void marcarComoPagada(int id) {
        String sql = "UPDATE multas SET pagada = true WHERE id = ?";

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConexionBD.getInstancia().getConexion();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Multa marcada como pagada. Filas afectadas: " + filasAfectadas);

        } catch (SQLException e) {
            System.out.println("Error al marcar multa como pagada");
            e.printStackTrace();
        } finally {
            cerrarRecursos(null, ps);
        }
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