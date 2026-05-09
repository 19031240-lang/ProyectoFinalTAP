package org.bibliotecafxv.dao;

import org.bibliotecafxv.model.Prestamo;
import org.bibliotecafxv.observer.AdminObserver;
import org.bibliotecafxv.observer.PrestamoNotifier;
import org.bibliotecafxv.connetion.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO implements GenericDAO<Prestamo> {

    private final Connection conn;

    private final PrestamoNotifier notifier;

    public PrestamoDAO() {

        conn = ConexionBD.getInstancia().getConexion();

        notifier = new PrestamoNotifier();

        notifier.agregarObserver(new AdminObserver());
    }

    @Override
    public void insertar(Prestamo prestamo) {

        String sql = """
                INSERT INTO prestamos
                (id_usuario, id_libro,
                 fecha_prestamo,
                 fecha_devolucion,
                 estado)
                 VALUES(?,?,?,?,?)
                """;

        try {

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, prestamo.getUsuarioId());
            ps.setInt(2, prestamo.getLibroId());

            ps.setDate(3,
                    Date.valueOf(prestamo.getFechaPrestamo()));

            ps.setDate(4,
                    Date.valueOf(prestamo.getFechaDevolucion()));

            ps.setString(5, prestamo.getEstado());

            ps.executeUpdate();

            notifier.notificar(
                    "Nuevo prestamo registrado"
            );

            System.out.println("Preestamo registrado");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Prestamo> listar() {

        List<Prestamo> lista = new ArrayList<>();

        String sql = "SELECT * FROM prestamos";

        try {

            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Prestamo p = new Prestamo();

                p.setId(rs.getInt("id"));
                p.setUsuarioId(rs.getInt("id_usuario"));
                p.setLibroId(rs.getInt("id_libro"));

                p.setFechaPrestamo(
                        rs.getDate("fecha_prestamo").toLocalDate()
                );

                p.setFechaDevolucion(
                        rs.getDate("fecha_devolucion").toLocalDate()
                );

                p.setEstado(rs.getString("estado"));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public Prestamo buscarPorId(int id) {
        return null;
    }

    @Override
    public void actualizar(Prestamo obj) {

    }

    @Override
    public void eliminar(int id) {

    }
}
