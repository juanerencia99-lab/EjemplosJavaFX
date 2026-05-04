package biblioteca.controlador;

import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;
import biblioteca.modelo.Modelo;
import biblioteca.vista.Vista;

import java.time.LocalDate;
import java.util.List;

/*
  Controlador:
  Intermediario entre Vista y Modelo.
  La Vista siempre llama al Controlador, y este delega en el Modelo.
*/
public class Controlador {

    private Modelo modelo;
    private Vista vista;

    public Controlador(Modelo modelo, Vista vista) {
        // Conecta Modelo y Vista. La Vista se queda con este controlador para llamar a sus métodos.
        if (modelo == null) {
            throw new IllegalArgumentException("Modelo nulo.");
        }
        if (vista == null) {
            throw new IllegalArgumentException("Vista nula.");
        }

        this.modelo = modelo;
        this.vista = vista;
        this.vista.setControlador(this);
    }

    public void comenzar() {
        // Arranca el modelo (crea colecciones) y luego arranca la vista (menú).
        modelo.comenzar();
        vista.comenzar();
    }

    public void terminar() {
        // Finaliza la aplicación llamando a vista y modelo y mostrando un mensaje final.
        vista.terminar();
        modelo.terminar();
        System.out.println("Termina Controlador...¡Hasta luego, Lucas!");
    }

    public void alta(Libro libro) {
        // Da de alta un libro delegando en el modelo.
        modelo.alta(libro);
    }

    public boolean baja(Libro libro) {
        // Da de baja un libro delegando en el modelo.
        return modelo.baja(libro);
    }

    public Libro buscar(Libro libro) {
        // Busca un libro y devuelve una copia si existe.
        return modelo.buscar(libro);
    }

    public List<Libro> listadoLibros() {
        // Devuelve el listado de libros (copias) del modelo.
        return modelo.listadoLibros();
    }

    public void alta(Usuario usuario) {
        // Da de alta un usuario delegando en el modelo.
        modelo.alta(usuario);
    }

    public boolean baja(Usuario usuario) {
        // Da de baja un usuario delegando en el modelo.
        return modelo.baja(usuario);
    }

    public Usuario buscar(Usuario usuario) {
        // Busca un usuario y devuelve una copia si existe.
        return modelo.buscar(usuario);
    }

    public List<Usuario> listadoUsuarios() {
        // Devuelve el listado de usuarios (copias) del modelo.
        return modelo.listadoUsuarios();
    }

    public void prestar(Libro libro, Usuario usuario, LocalDate fecha) {
        // Registra un préstamo delegando en el modelo.
        modelo.prestar(libro, usuario, fecha);
    }

    public boolean devolver(Libro libro, Usuario usuario, LocalDate fecha) {
        // Devuelve un préstamo delegando en el modelo.
        return modelo.devolver(libro, usuario, fecha);
    }

    public List<Prestamo> listadoPrestamos(Usuario usuario) {
        // Devuelve el listado de préstamos de un usuario.
        return modelo.listadoPrestamos(usuario);
    }

    public List<Prestamo> listadoPrestamos() {
        // Devuelve el listado completo de préstamos.
        return modelo.listadoPrestamos();
    }
}
