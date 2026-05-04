package biblioteca.modelo;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.negocio.Libros;
import biblioteca.modelo.negocio.Prestamos;
import biblioteca.modelo.negocio.Usuarios;

import java.time.LocalDate;
import java.util.List;

public class Modelo {

    private Libros libros;
    private Usuarios usuarios;
    private Prestamos prestamos;

    public Modelo() {
        libros = Libros.getInstancia();
        usuarios = Usuarios.getInstancia();
        prestamos = Prestamos.getInstancia();
    }

    public void comenzar() {
        usuarios.comenzar();
        libros.comenzar();
        prestamos.comenzar();
    }

    public void terminar() {
        prestamos.terminar();
        libros.terminar();
        usuarios.terminar();
        System.out.println("Termina Modelo...Hasta luego, Lucas!");
    }

    public void alta(Libro libro) {
        if (libros == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        libros.alta(libro);
    }

    public boolean baja(Libro libro) {
        if (libros == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return libros.baja(libro);
    }

    public Libro buscar(Libro libro) {
        if (libros == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return libros.buscar(libro);
    }

    public List<Libro> listadoLibros() {
        if (libros == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return libros.todos();
    }

    public void alta(Usuario usuario) {
        if (usuarios == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        usuarios.alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        if (usuarios == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return usuarios.baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        if (usuarios == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return usuarios.buscar(usuario);
    }

    public List<Usuario> listadoUsuarios() {
        if (usuarios == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return usuarios.todos();
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        if (prestamos == null || libros == null || usuarios == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Fecha nula.");
        }

        Libro libroPersistido = libros.buscar(libro);
        if (libroPersistido == null) {
            throw new IllegalArgumentException("El libro no existe.");
        }

        Usuario usuarioPersistido = usuarios.buscar(usuario);
        if (usuarioPersistido == null) {
            throw new IllegalArgumentException("El usuario no existe.");
        }

        // Se usan los objetos recuperados de la BD para asegurar que el prestamo
        // trabaja con entidades existentes y completas, no solo con una "clave" temporal.
        prestamos.prestar(libroPersistido, usuarioPersistido, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        if (prestamos == null || libros == null || usuarios == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("Fecha nula.");
        }
        return prestamos.devolver(libro, usuario, fecha);
    }

    public List<Prestamo> listadoPrestamos(Usuario usuario) {
        if (prestamos == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return prestamos.todos(usuario);
    }

    public List<Prestamo> listadoPrestamos() {
        if (prestamos == null) {
            throw new IllegalStateException("Modelo no iniciado.");
        }
        return prestamos.todos();
    }
}
