package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Categoria;
import biblioteca.modelo.dominio.Direccion;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prestamos {

    private static final String SQL_PRESTAR =
            "INSERT INTO prestamo (dni, isbn, fInicio, fLimite, devuelto, fDevolucion) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_COMPROBAR_PRESTAMO_ACTIVO =
            "SELECT COUNT(*) total FROM prestamo WHERE dni = ? AND isbn = ? AND devuelto = 0";
    private static final String SQL_DEVOLVER =
            "UPDATE prestamo SET devuelto = 1, fDevolucion = ? WHERE dni = ? AND isbn = ? AND devuelto = 0";
    private static final String SQL_LISTAR_PRESTAMOS_USUARIO =
            "SELECT dni, isbn, fInicio, devuelto, fDevolucion FROM prestamo WHERE dni = ? ORDER BY fInicio DESC, isbn";
    private static final String SQL_LISTAR_PRESTAMOS =
            "SELECT dni, isbn, fInicio, devuelto, fDevolucion FROM prestamo ORDER BY fInicio DESC, dni, isbn";

    private static final Prestamos INSTANCIA = new Prestamos();

    private Connection conexion;

    private Prestamos() {
    }

    public static Prestamos getInstancia() {
        return INSTANCIA;
    }

    public void comenzar() {
        conexion = Conexion.establecerConexion();
    }

    public void terminar() {
        Conexion.cerrarConexion();
        conexion = null;
    }

    public Prestamo prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo.");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Fecha nula.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentenciaComprobacion = conexion.prepareStatement(SQL_COMPROBAR_PRESTAMO_ACTIVO)) {
            sentenciaComprobacion.setString(1, usuario.getDni());
            sentenciaComprobacion.setString(2, libro.getIsbn().replace("-", ""));

            try (ResultSet consulta = sentenciaComprobacion.executeQuery()) {
                // Evita duplicar un prestamo del mismo libro mientras siga pendiente de devolucion.
                if (consulta.next() && consulta.getInt("total") > 0) {
                    throw new IllegalStateException("El usuario ya tiene este libro prestado y no lo ha devuelto.");
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo comprobar el estado del prestamo.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_PRESTAR)) {
            sentencia.setString(1, usuario.getDni());
            sentencia.setString(2, libro.getIsbn().replace("-", ""));
            sentencia.setDate(3, Date.valueOf(fecha));
            sentencia.setDate(4, Date.valueOf(fecha.plusDays(15)));
            sentencia.setBoolean(5, false);
            sentencia.setDate(6, null);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo registrar el prestamo en la base de datos.", e);
        }

        return new Prestamo(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo.");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Fecha nula.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_DEVOLVER)) {
            sentencia.setDate(1, Date.valueOf(fecha));
            sentencia.setString(2, usuario.getDni());
            sentencia.setString(3, libro.getIsbn().replace("-", ""));
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo devolver el prestamo en la base de datos.", e);
        }
    }

    public List<Prestamo> todos(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }

        List<Prestamo> resultado = new ArrayList<>();

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR_PRESTAMOS_USUARIO)) {
            sentencia.setString(1, usuario.getDni());

            try (ResultSet consulta = sentencia.executeQuery()) {
                while (consulta.next()) {
                    Usuario usuarioPrestamo = Usuarios.getInstancia().buscar(
                            new Usuario(consulta.getString("dni"), "", "", new Direccion("", "", "", ""))
                );

                    String isbnSinGuiones = consulta.getString("isbn");
                    // La tabla guarda el ISBN sin guiones, asi que aqui se reconstruye para crear el objeto de dominio.
                    String isbnConGuiones = isbnSinGuiones.substring(0, 3) + "-"
                            + isbnSinGuiones.substring(3, 5) + "-"
                            + isbnSinGuiones.substring(5, 10) + "-"
                            + isbnSinGuiones.substring(10, 12) + "-"
                            + isbnSinGuiones.substring(12);

                    Libro libroPrestamo = Libros.getInstancia().buscar(
                            new Libro(isbnConGuiones, "", 0, Categoria.OTROS)
                    );

                    if (usuarioPrestamo == null || libroPrestamo == null) {
                        throw new IllegalStateException("Los datos relacionados del prestamo no existen en la base de datos.");
                    }

                    Prestamo prestamo = new Prestamo(
                            libroPrestamo,
                            usuarioPrestamo,
                            consulta.getDate("fInicio").toLocalDate()
                    );

                    if (consulta.getBoolean("devuelto")) {
                        Date fechaDevolucion = consulta.getDate("fDevolucion");
                        if (fechaDevolucion != null) {
                            prestamo.marcarDevuelto(fechaDevolucion.toLocalDate());
                        }
                    }

                    resultado.add(prestamo);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar los prestamos del usuario.", e);
        }

        return resultado;
    }

    public List<Prestamo> todos() {
        List<Prestamo> resultado = new ArrayList<>();

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR_PRESTAMOS);
             ResultSet consulta = sentencia.executeQuery()) {
            while (consulta.next()) {
                Usuario usuarioPrestamo = Usuarios.getInstancia().buscar(
                        new Usuario(consulta.getString("dni"), "", "", new Direccion("", "", "", ""))
                );

                String isbnSinGuiones = consulta.getString("isbn");
                String isbnConGuiones = isbnSinGuiones.substring(0, 3) + "-"
                        + isbnSinGuiones.substring(3, 5) + "-"
                        + isbnSinGuiones.substring(5, 10) + "-"
                        + isbnSinGuiones.substring(10, 12) + "-"
                        + isbnSinGuiones.substring(12);

                Libro libroPrestamo = Libros.getInstancia().buscar(
                        new Libro(isbnConGuiones, "", 0, Categoria.OTROS)
                );

                if (usuarioPrestamo == null || libroPrestamo == null) {
                    throw new IllegalStateException("Los datos relacionados del prestamo no existen en la base de datos.");
                }

                Prestamo prestamo = new Prestamo(
                        libroPrestamo,
                        usuarioPrestamo,
                        consulta.getDate("fInicio").toLocalDate()
                );

                if (consulta.getBoolean("devuelto")) {
                    Date fechaDevolucion = consulta.getDate("fDevolucion");
                    if (fechaDevolucion != null) {
                        prestamo.marcarDevuelto(fechaDevolucion.toLocalDate());
                    }
                }

                resultado.add(prestamo);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar los prestamos de la base de datos.", e);
        }

        return resultado;
    }
}
