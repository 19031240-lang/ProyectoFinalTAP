package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Multa;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MultaDAO implements GenericDAO<Multa> {

    private final Connection conn;

    public MultaDAO() {

        conn = ConexionBD.getInstancia().getConexion();
    }

    @Override
    public void insertar(Multa multa) {

        String sql =
                "INSERT INTO multas(prestamo_id,monto,pagada) VALUES(?,?,?)";

        try {

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, multa.getPrestamoId());
            ps.setDouble(2, multa.getMonto());
            ps.setBoolean(3, multa.isPagada());

            ps.executeUpdate();

            System.out.println("Multa registrada");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Multa> listar() {

        List<Multa> lista = new ArrayList<>();

        String sql = "SELECT * FROM multas";

        try {

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Multa m = new Multa();

                m.setId(rs.getInt("id"));
                m.setPrestamoId(rs.getInt("prestamo_id"));
                m.setMonto(rs.getDouble("monto"));
                m.setPagada(rs.getBoolean("pagada"));

                lista.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public Multa buscarPorId(int id) {
        return null;
    }

    @Override
    public void actualizar(Multa obj) {

    }

    @Override
    public void eliminar(int id) {

    }
}
