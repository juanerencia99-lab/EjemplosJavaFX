package biblioteca.modelo.negocio.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String HOST = "localhost:3306";
    private static final String ESQUEMA = "biblioteca";
    private static final String USUARIO = "admin";
    private static final String CONTRASENA = "biblioteca-2026";
    // Estos parametros evitan problemas frecuentes del driver con zona horaria,
    // SSL en local y autenticacion de MySQL 8.
    private static final String URL = "jdbc:mysql://" + HOST + "/" + ESQUEMA
            + "?serverTimezone=Europe/Madrid&useSSL=false&allowPublicKeyRetrieval=true";

    // Se reutiliza una unica conexion mientras la aplicacion siga abierta.
    private static Connection conexion;

    private Conexion() {
    }

    public static Connection establecerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
                System.out.println("Conexion a MySQL realizada correctamente.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo abrir la conexion con MySQL.", e);
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexion a MySQL cerrada correctamente.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo cerrar la conexion con MySQL.", e);
        } finally {
            // Aunque close falle, dejamos la referencia a null para no reutilizar un estado inconsistente.
            conexion = null;
        }
    }
}
