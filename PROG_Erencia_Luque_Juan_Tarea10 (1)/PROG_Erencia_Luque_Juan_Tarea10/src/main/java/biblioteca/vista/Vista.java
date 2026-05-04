package biblioteca.vista;

import biblioteca.Entrada;
import biblioteca.controlador.Controlador;
import biblioteca.modelo.dominio.Libro;
import biblioteca.modelo.dominio.Prestamo;
import biblioteca.modelo.dominio.Usuario;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
  Vista:
  - Gestiona el menú y el bucle principal.
  - Pide datos a Consola y llama al Controlador.
  - Captura excepciones para que la aplicación no se cierre por errores de entrada o negocio.
*/
public class Vista {

    private Controlador controlador;

    public Vista() {
        // Constructor sin parámetros. Consola es ahora estática.
    }

    public void setControlador(Controlador controlador) {
        // Guarda la referencia del controlador para poder invocar operaciones del modelo.
        if (controlador == null) {
            throw new IllegalArgumentException("Controlador nulo.");
        }
        this.controlador = controlador;
    }

    public void comenzar() {
        // Bucle principal: muestra menú, lee opcion y ejecuta hasta que el usuario elija SALIR.
        Opcion opcion = null;

        while (opcion != Opcion.SALIR) {
            Consola.mostrarMenu();

            // Lee un número y lo valida para que sea un índice válido del enum Opcion.
            int n = -1;
            while (n < 0 || n >= Opcion.values().length) {
                System.out.print("Elige opcion (0-" + (Opcion.values().length - 1) + "): ");
                n = Entrada.entero();
                if (n < 0 || n >= Opcion.values().length) {
                    System.out.println("Opcion invalida.");
                }
            }
            // Convierte el número a la opción correspondiente y la ejecuta.
            opcion = Opcion.values()[n];
            ejecutarOpcion(opcion);
        }
    }

    public void terminar() {
        // Mensajes de finalización de la capa de vista y consola.
        System.out.println("Termina consola..¡Hasta luego, Lucas!");
        System.out.println("Termina Vista.. ¡Hasta luego, Lucas!");
    }

    private void ejecutarOpcion(Opcion opcion) {
        // Ejecuta la acción asociada a la opción elegida del menu.
        switch (opcion) {
            case INSERTAR_USUARIO:
                insertarUsuario();
                break;
            case BORRAR_USUARIO:
                borrarUsuario();
                break;
            case MOSTRAR_USUARIOS:
                mostrarUsuarios();
                break;
            case INSERTAR_LIBRO:
                insertarLibro();
                break;
            case BORRAR_LIBRO:
                borrarLibro();
                break;
            case MOSTRAR_LIBROS:
                mostrarLibros();
                break;
            case NUEVO_PRESTAMO:
                nuevoPrestamo();
                break;
            case DEVOLVER_PRESTAMO:
                devolverPrestamo();
                break;
            case MOSTRAR_PRESTAMOS:
                mostrarPrestamos();
                break;
            case MOSTRAR_PRESTAMOS_USUARIOS:
                mostrarPrestamosUsuario();
                break;
            case SALIR:
                try {
                    controlador.terminar();
                } catch (Exception e) {
                    System.out.println("Error al terminar: " + e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    private void insertarUsuario() {
        // Pide los datos del usuario, intenta darlo de alta y muestra el resultado.
        try {
            Usuario u = Consola.nuevoUsuario(false);
            controlador.alta(u);
            System.out.println("Usuario insertado correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo insertar el usuario: " + e.getMessage());
        }
    }

    private void borrarUsuario() {
        // Pide la "clave" del usuario (modo buscar), intenta borrarlo y avisa si existía o no.
        try {
            Usuario u = Consola.nuevoUsuario(true);
            boolean ok = controlador.baja(u);
            if (ok) {
                System.out.println("Usuario borrado correctamente.");
            } else {
                System.out.println("No existe ese usuario.");
            }
        } catch (Exception e) {
            System.out.println("No se pudo borrar el usuario: " + e.getMessage());
        }
    }

    private void mostrarUsuarios() {
        // Recupera el listado completo de usuarios, lo ordena por nombre y lo imprime por pantalla.
        try {
            List<Usuario> us = controlador.listadoUsuarios();
            
            // Ordena internamente de forma alfabética con el metodo compareTo
            Collections.sort(us);
            
            System.out.println("=== USUARIOS ===");
            for (Usuario u : us) {
                System.out.println(u);
            }
        } catch (Exception e) {
            System.out.println("No se pudieron mostrar usuarios: " + e.getMessage());
        }
    }

    private void insertarLibro() {
        // Pide los datos del libro, intenta darlo de alta y muestra el resultado
        try {
            Libro l = Consola.nuevoLibro(false);
            controlador.alta(l);
            System.out.println("Libro insertado correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo insertar el libro: " + e.getMessage());
        }
    }

    private void borrarLibro() {
        // Pide la "clave" del libro (modo buscar), intenta borrarlo y avisa si existía o no.
        try {
            Libro l = Consola.nuevoLibro(true);
            boolean ok = controlador.baja(l);
            if (ok) {
                System.out.println("Libro borrado correctamente.");
            } else {
                System.out.println("No existe ese libro.");
            }
        } catch (Exception e) {
            System.out.println("No se pudo borrar el libro: " + e.getMessage());
        }
    }

    private void mostrarLibros() {
        // Recupera el listado completo de libros, lo ordena por título y lo imprime por pantalla.
        try {
            List<Libro> ls = controlador.listadoLibros();
            
            // Ordenar internamente de forma alfabética con el metodo compareTo.
            Collections.sort(ls);
            
            System.out.println("=== LIBROS ===");
            for (Libro l : ls) {
                System.out.println(l);
            }
        } catch (Exception e) {
            System.out.println("No se pudieron mostrar libros: " + e.getMessage());
        }
    }

    private void nuevoPrestamo() {
        // Pide libro y usuario (modo buscar), los busca en el sistema y registra un préstamo con la fecha indicada
        try {
            // Crear objetos "clave" solo con ISBN y DNI para buscar
            Libro libroClave = Consola.nuevoLibro(true);
            Usuario usuarioClave = Consola.nuevoUsuario(true);

            // Buscar los objetos reales en el sistema
            Libro libroReal = controlador.buscar(libroClave);
            Usuario usuarioReal = controlador.buscar(usuarioClave);

            // Verificar que ambos existen
            if (libroReal == null) {
                System.out.println("No se encontro el libro en el sistema.");
                return;
            }
            if (usuarioReal == null) {
                System.out.println("No se encontro el usuario en el sistema.");
                return;
            }

            // Registrar el préstamo con los objetos reales
            controlador.prestar(libroReal, usuarioReal, Consola.leerFecha());
            System.out.println("Prestamo registrado correctamente.");
        } catch (Exception e) {
            System.out.println("No se pudo registrar el prestamo: " + e.getMessage());
        }
    }

    private void devolverPrestamo() {
        // Pide libro y usuario (modo buscar), los busca en el sistema e intenta cerrar el préstamo activo con una fecha de devolución.
        try {
            // Crear objetos "clave" solo con ISBN y DNI para buscar
            Libro libroClave = Consola.nuevoLibro(true);
            Usuario usuarioClave = Consola.nuevoUsuario(true);

            // Buscar los objetos reales en el sistema
            Libro libroReal = controlador.buscar(libroClave);
            Usuario usuarioReal = controlador.buscar(usuarioClave);

            // Verificar que ambos existen
            if (libroReal == null) {
                System.out.println("No se encontro el libro en el sistema.");
                return;
            }
            if (usuarioReal == null) {
                System.out.println("No se encontro el usuario en el sistema.");
                return;
            }

            // Intentar devolver el préstamo con los objetos reales
            boolean ok = controlador.devolver(libroReal, usuarioReal, Consola.leerFecha());
            if (ok) {
                System.out.println("Prestamo devuelto correctamente.");
            } else {
                System.out.println("No se encontro un prestamo activo para devolver.");
            }
        } catch (Exception e) {
            System.out.println("No se pudo devolver el prestamo: " + e.getMessage());
        }
    }

    private void mostrarPrestamos() {
        // Recupera el listado completo de préstamos, lo ordena y lo imprime por pantalla.
        try {
            List<Prestamo> ps = controlador.listadoPrestamos();
            
            // Ordenar por fecha de inicio descendente, luego por nombre de usuario A-Z
            ps.sort(Comparator
                    .comparing(Prestamo::getFinicio).reversed()
                    .thenComparing(p -> p.getUsuario().getNombre()));
            
            System.out.println("=== PRESTAMOS ===");
            for (Prestamo p : ps) {
                System.out.println(p);
            }
        } catch (Exception e) {
            System.out.println("No se pudieron mostrar prestamos: " + e.getMessage());
        }
    }

    private void mostrarPrestamosUsuario() {
        // Pide un usuario (modo buscar) y muestra solo los préstamos asociados a ese usuario ordenados.
        try {
            Usuario u = Consola.nuevoUsuario(true);
            List<Prestamo> ps = controlador.listadoPrestamos(u);
            
            // Ordenar por fecha de inicio descendente, luego por nombre de usuario A-Z
            ps.sort(Comparator
                    .comparing(Prestamo::getFinicio).reversed()
                    .thenComparing(p -> p.getUsuario().getNombre()));
            
            System.out.println("=== PRESTAMOS DEL USUARIO ===");
            for (Prestamo p : ps) {
                System.out.println(p);
            }
        } catch (Exception e) {
            System.out.println("No se pudieron mostrar los prestamos del usuario: " + e.getMessage());
        }
    }
}
