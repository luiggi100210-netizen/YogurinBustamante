package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton que gestiona la conexion unica a la base de datos MySQL.
 * Garantiza que solo exista una instancia de conexion durante toda la sesion.
 *
 * Base de datos: yogurin_db
 * Puerto por defecto: 3306
 *
 * @author Luiggi
 */
public class Conexion {

    /** URL de conexion JDBC a la base de datos */
    private static final String URL = "jdbc:mysql://localhost:3306/yogurin_db?useSSL=false&serverTimezone=UTC";

    /** Usuario de la base de datos */
    private static final String USUARIO = "root";

    /** Contrasena de la base de datos */
    private static final String CONTRASENA = "";

    /** Instancia unica de la conexion */
    private static Connection instancia = null;

    /** Constructor privado — impide instanciacion externa */
    private Conexion() {}

    /**
     * Retorna la conexion activa. Si no existe o esta cerrada, la crea.
     *
     * @return Conexion activa a MySQL
     * @throws SQLException si no se puede establecer la conexion
     */
    public static Connection getInstancia() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instancia = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
            }
        }
        return instancia;
    }

    /**
     * Cierra la conexion de forma segura y libera recursos.
     */
    public static void cerrar() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                instancia = null;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}
