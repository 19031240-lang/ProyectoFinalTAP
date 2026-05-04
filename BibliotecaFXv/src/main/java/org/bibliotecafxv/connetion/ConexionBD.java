package org.bibliotecafxv.connetion;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3307/libreria";
    private static final String USER = "libreria";
    private static final String PASSWORD = "1234";

    public static Connection conectar() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Conexión exitosa a la BD");
            return conn;
        } catch (Exception e) {
            System.out.println(" Error al conectar");
            e.printStackTrace();
            return null;
        }
    }
}