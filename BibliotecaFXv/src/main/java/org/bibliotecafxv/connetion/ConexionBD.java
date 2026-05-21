package org.bibliotecafxv.connetion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión física con la base de datos MySQL.
 * Implementa el **Patrón de Diseño Singleton** para asegurar que exista una única
 * instancia de la conexión en toda la aplicación, optimizando el consumo de recursos.
 */
public class ConexionBD {

    private static ConexionBD instancia;
    private Connection conexion;

    private final String URL = "jdbc:mysql://localhost:3307/libreria";
    private final String USER = "libreria";
    private final String PASSWORD = "1234";

    /**
     * Constructor privado para evitar la instanciación externa directa (regala del Singleton).
     * Inicializa el driver de conexión a MySQL.
     */
    private ConexionBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver MySQL no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error de conexión a la base de datos");
            e.printStackTrace();
        }
    }

    /**
     * Proporciona el punto de acceso global a la instancia única de ConexionBD.
     * Si la instancia aún no existe, la crea por primera vez.
     * * @return La instancia única y centralizada de esta clase.
     */
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    /**
     * Obtiene la conexión activa a la base de datos. Si la conexión fue cerrada
     * o se cayó por inactividad, se encarga de reestablecerla automáticamente.
     * * @return Objeto Connection listo para ejecutar sentencias SQL.
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión reestablecida");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conexion;
    }

    /**
     * Cierra de forma segura la conexión activa con el servidor de base de datos
     * si esta se encuentra abierta.
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    System.out.println("Conexión cerrada correctamente");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}