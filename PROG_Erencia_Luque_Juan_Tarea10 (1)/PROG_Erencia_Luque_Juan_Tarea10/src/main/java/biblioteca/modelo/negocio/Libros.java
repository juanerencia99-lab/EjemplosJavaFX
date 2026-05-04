package biblioteca.modelo.negocio;

import biblioteca.modelo.dominio.Audiolibro;
import biblioteca.modelo.dominio.Autor;
import biblioteca.modelo.dominio.Categoria;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.negocio.mysql.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Libros {

    private static final String SQL_INSERTAR_LIBRO =
            "INSERT INTO libro (isbn, titulo, anio, categoria) VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERTAR_AUDIOLIBRO =
            "INSERT INTO audiolibro (isbn, duracion_segundos, formato) VALUES (?, ?, ?)";
    private static final String SQL_BORRAR_LIBRO =
            "DELETE FROM libro WHERE isbn = ?";
    private static final String SQL_BUSCAR_LIBRO =
            "SELECT l.isbn, l.titulo, l.anio, l.categoria, a.duracion_segundos, a.formato "
                    + "FROM libro l LEFT JOIN audiolibro a ON l.isbn = a.isbn "
                    + "WHERE l.isbn = ?";
    private static final String SQL_LISTAR_LIBROS =
            "SELECT l.isbn, l.titulo, l.anio, l.categoria, a.duracion_segundos, a.formato "
                    + "FROM libro l LEFT JOIN audiolibro a ON l.isbn = a.isbn "
                    + "ORDER BY l.titulo, l.isbn";
    private static final String SQL_BUSCAR_AUTOR =
            "SELECT idAutor FROM autor WHERE nombre = ? AND apellidos = ? AND nacionalidad = ?";
    private static final String SQL_INSERTAR_AUTOR =
            "INSERT INTO autor (nombre, apellidos, nacionalidad) VALUES (?, ?, ?)";
    private static final String SQL_INSERTAR_LIBRO_AUTOR =
            "INSERT INTO libro_autor (isbn, idAutor) VALUES (?, ?)";
    private static final String SQL_BUSCAR_AUTORES_LIBRO =
            "SELECT a.nombre, a.apellidos, a.nacionalidad "
                    + "FROM libro_autor la INNER JOIN autor a ON la.idAutor = a.idAutor "
                    + "WHERE la.isbn = ? ORDER BY a.apellidos, a.nombre, a.idAutor";

    private static final Libros INSTANCIA = new Libros();

    private Connection conexion;

    private Libros() {
    }

    public static Libros getInstancia() {
        return INSTANCIA;
    }

    public void comenzar() {
        conexion = Conexion.establecerConexion();
    }

    public void terminar() {
        Conexion.cerrarConexion();
        conexion = null;
    }

    public void alta(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo.");
        }

        if (buscar(libro) != null) {
            throw new IllegalArgumentException("Ya existe un libro con ese ISBN.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement insertarLibro = conexion.prepareStatement(SQL_INSERTAR_LIBRO)) {
            // En la BD se guarda el ISBN sin guiones para mantener un formato unico.
            insertarLibro.setString(1, libro.getIsbn().replace("-", ""));
            insertarLibro.setString(2, libro.getTitulo());
            insertarLibro.setInt(3, libro.getAnio());
            insertarLibro.setString(4, libro.getCategoria().name());
            insertarLibro.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo insertar el libro en la base de datos.", e);
        }

        try {
            if (libro instanceof Audiolibro) {
                try (PreparedStatement insertarAudiolibro = conexion.prepareStatement(SQL_INSERTAR_AUDIOLIBRO)) {
                    insertarAudiolibro.setString(1, libro.getIsbn().replace("-", ""));
                    insertarAudiolibro.setLong(2, ((Audiolibro) libro).getDuracion().getSeconds());
                    insertarAudiolibro.setString(3, ((Audiolibro) libro).getFormato());
                    insertarAudiolibro.executeUpdate();
                }
            }

            for (Autor autor : libro.getAutores()) {
                int idAutor = -1;

                try (PreparedStatement buscarAutor = conexion.prepareStatement(SQL_BUSCAR_AUTOR)) {
                    buscarAutor.setString(1, autor.getNombre());
                    buscarAutor.setString(2, autor.getApellidos());
                    buscarAutor.setString(3, autor.getNacionalidad());

                    try (ResultSet consulta = buscarAutor.executeQuery()) {
                        if (consulta.next()) {
                            idAutor = consulta.getInt("idAutor");
                        }
                    }
                }

                if (idAutor == -1) {
                    try (PreparedStatement insertarAutor = conexion.prepareStatement(SQL_INSERTAR_AUTOR, Statement.RETURN_GENERATED_KEYS)) {
                        insertarAutor.setString(1, autor.getNombre());
                        insertarAutor.setString(2, autor.getApellidos());
                        insertarAutor.setString(3, autor.getNacionalidad());
                        insertarAutor.executeUpdate();

                        try (ResultSet claves = insertarAutor.getGeneratedKeys()) {
                            // Al insertar un autor nuevo, MySQL genera su id automaticamente.
                            // Aqui lo recuperamos para poder guardarlo despues en la tabla libro_autor.
                            if (claves.next()) {
                                idAutor = claves.getInt(1);
                            }
                        }
                    }
                }

                if (idAutor == -1) {
                    throw new IllegalStateException("No se pudo obtener el identificador del autor.");
                }

                try (PreparedStatement insertarLibroAutor = conexion.prepareStatement(SQL_INSERTAR_LIBRO_AUTOR)) {
                    insertarLibroAutor.setString(1, libro.getIsbn().replace("-", ""));
                    insertarLibroAutor.setInt(2, idAutor);
                    insertarLibroAutor.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo insertar el libro en la base de datos.", e);
        }
    }

    public boolean baja(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BORRAR_LIBRO)) {
            sentencia.setString(1, libro.getIsbn().replace("-", ""));
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo borrar el libro de la base de datos.", e);
        }
    }

    public Libro buscar(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo.");
        }

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_LIBRO)) {
            sentencia.setString(1, libro.getIsbn().replace("-", ""));

            try (ResultSet consulta = sentencia.executeQuery()) {
                if (consulta.next()) {
                    String isbnSinGuiones = consulta.getString("isbn");
                    // En el dominio se vuelve a mostrar el ISBN con guiones para conservar el formato habitual.
                    String isbnConGuiones = isbnSinGuiones.substring(0, 3) + "-"
                            + isbnSinGuiones.substring(3, 5) + "-"
                            + isbnSinGuiones.substring(5, 10) + "-"
                            + isbnSinGuiones.substring(10, 12) + "-"
                            + isbnSinGuiones.substring(12);

                    Libro libroEncontrado;
                    Long duracionSegundos = (Long) consulta.getObject("duracion_segundos");

                    // Si la parte especifica del LEFT JOIN existe, reconstruimos un Audiolibro; si no, un Libro normal.
                    if (duracionSegundos != null) {
                        libroEncontrado = new Audiolibro(
                                isbnConGuiones,
                                consulta.getString("titulo"),
                                consulta.getInt("anio"),
                                Categoria.valueOf(consulta.getString("categoria")),
                                Duration.ofSeconds(duracionSegundos),
                                consulta.getString("formato")
                        );
                    } else {
                        libroEncontrado = new Libro(
                                isbnConGuiones,
                                consulta.getString("titulo"),
                                consulta.getInt("anio"),
                                Categoria.valueOf(consulta.getString("categoria"))
                        );
                    }

                    try (PreparedStatement sentenciaAutores = conexion.prepareStatement(SQL_BUSCAR_AUTORES_LIBRO)) {
                        sentenciaAutores.setString(1, isbnSinGuiones);

                        try (ResultSet consultaAutores = sentenciaAutores.executeQuery()) {
                            while (consultaAutores.next()) {
                                libroEncontrado.addAutor(new Autor(
                                        consultaAutores.getString("nombre"),
                                        consultaAutores.getString("apellidos"),
                                        consultaAutores.getString("nacionalidad")
                                ));
                            }
                        }
                    }

                    return libroEncontrado;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el libro en la base de datos.", e);
        }

        return null;
    }

    public List<Libro> todos() {
        List<Libro> resultado = new ArrayList<>();

        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = Conexion.establecerConexion();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo obtener una conexion activa con MySQL.", e);
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR_LIBROS);
             ResultSet consulta = sentencia.executeQuery()) {
            while (consulta.next()) {
                String isbnSinGuiones = consulta.getString("isbn");
                String isbnConGuiones = isbnSinGuiones.substring(0, 3) + "-"
                        + isbnSinGuiones.substring(3, 5) + "-"
                        + isbnSinGuiones.substring(5, 10) + "-"
                        + isbnSinGuiones.substring(10, 12) + "-"
                        + isbnSinGuiones.substring(12);

                Libro libro;
                Long duracionSegundos = (Long) consulta.getObject("duracion_segundos");

                if (duracionSegundos != null) {
                    libro = new Audiolibro(
                            isbnConGuiones,
                            consulta.getString("titulo"),
                            consulta.getInt("anio"),
                            Categoria.valueOf(consulta.getString("categoria")),
                            Duration.ofSeconds(duracionSegundos),
                            consulta.getString("formato")
                    );
                } else {
                    libro = new Libro(
                            isbnConGuiones,
                            consulta.getString("titulo"),
                            consulta.getInt("anio"),
                            Categoria.valueOf(consulta.getString("categoria"))
                    );
                }

                try (PreparedStatement sentenciaAutores = conexion.prepareStatement(SQL_BUSCAR_AUTORES_LIBRO)) {
                    sentenciaAutores.setString(1, isbnSinGuiones);

                    try (ResultSet consultaAutores = sentenciaAutores.executeQuery()) {
                        while (consultaAutores.next()) {
                            libro.addAutor(new Autor(
                                    consultaAutores.getString("nombre"),
                                    consultaAutores.getString("apellidos"),
                                    consultaAutores.getString("nacionalidad")
                            ));
                        }
                    }
                }

                resultado.add(libro);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar los libros de la base de datos.", e);
        }

        return resultado;
    }
}
