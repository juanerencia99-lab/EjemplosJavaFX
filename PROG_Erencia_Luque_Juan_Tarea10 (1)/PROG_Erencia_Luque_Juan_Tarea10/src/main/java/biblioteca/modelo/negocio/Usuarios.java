package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Direccion;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Usuarios {

    private static final String SQL_INSERTAR_USUARIO =
            "INSERT INTO usuario (dni, nombre, email) VALUES (?, ?, ?)";
    private static final String SQL_INSERTAR_DIRECCION =
            "INSERT INTO direccion (dni, via, numero, cp, localidad) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_BORRAR_USUARIO =
            "DELETE FROM usuario WHERE dni = ?";
    private static final String SQL_BUSCAR_USUARIO =
            "SELECT u.dni, u.nombre, u.email, d.via, d.numero, d.cp, d.localidad "
                    + "FROM usuario u INNER JOIN direccion d ON u.dni = d.dni "
                    + "WHERE u.dni = ?";
    private static final String SQL_LISTAR_USUARIOS =
            "SELECT u.dni, u.nombre, u.email, d.via, d.numero, d.cp, d.localidad "
                    + "FROM usuario u INNER JOIN direccion d ON u.dni = d.dni "
                    + "ORDER BY u.nombre, u.dni";

    private static final Usuarios INSTANCIA = new Usuarios();

    private Connection conexion;

    private Usuarios() {
    }

    public static Usuarios getInstancia() {
        return INSTANCIA;
    }

    public void comenzar() {
        conexion = Conexion.establecerConexion();
    }

    public void terminar() {
        Conexion.cerrarConexion();
        conexion = null;
    }

    public void alta(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }

        if (buscar(usuario) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese DNI.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement insertarUsuario = conexion.prepareStatement(SQL_INSERTAR_USUARIO);
             PreparedStatement insertarDireccion = conexion.prepareStatement(SQL_INSERTAR_DIRECCION)) {
            insertarUsuario.setString(1, usuario.getDni());
            insertarUsuario.setString(2, usuario.getNombre());
            insertarUsuario.setString(3, usuario.getEmail());
            insertarUsuario.executeUpdate();

            Direccion direccion = usuario.getDireccion();
            insertarDireccion.setString(1, usuario.getDni());
            insertarDireccion.setString(2, direccion.getVia());
            insertarDireccion.setString(3, direccion.getNumero());
            insertarDireccion.setString(4, direccion.getCp());
            insertarDireccion.setString(5, direccion.getLocalidad());
            insertarDireccion.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo insertar el usuario en la base de datos.", e);
        }
    }

    public boolean baja(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BORRAR_USUARIO)) {
            sentencia.setString(1, usuario.getDni());
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo borrar el usuario de la base de datos.", e);
        }
    }

    public Usuario buscar(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_USUARIO)) {
            sentencia.setString(1, usuario.getDni());
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    Direccion direccion = new Direccion(
                            resultado.getString("via"),
                            resultado.getString("numero"),
                            resultado.getString("cp"),
                            resultado.getString("localidad")
                    );

                    return new Usuario(
                            resultado.getString("dni"),
                            resultado.getString("nombre"),
                            resultado.getString("email"),
                            direccion
                    );
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el usuario en la base de datos.", e);
        }

        return null;
    }

    public List<Usuario> todos() {
        List<Usuario> resultado = new ArrayList<>();

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR_USUARIOS);
             ResultSet consulta = sentencia.executeQuery()) {
            while (consulta.next()) {
                Direccion direccion = new Direccion(
                        consulta.getString("via"),
                        consulta.getString("numero"),
                        consulta.getString("cp"),
                        consulta.getString("localidad")
                );

                resultado.add(new Usuario(
                        consulta.getString("dni"),
                        consulta.getString("nombre"),
                        consulta.getString("email"),
                        direccion
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar los usuarios de la base de datos.", e);
        }

        return resultado;
    }
}
