package biblioteca.vista;

import biblioteca.Entrada;
import biblioteca.modelo.dominio.*;

import java.time.Duration;
import java.time.LocalDate;

/*
  Consola:
  - Muestra menús.
  - Lee datos por teclado usando Entrada.
  - Crea objetos de dominio. Si los datos no son válidos, el propio objeto lanzará excepción.
  - Clase estática: todos los métodos son estáticos y el constructor es privado.
*/
public class Consola {

    private Consola() {
        // Constructor privado para evitar instanciación.
    }

    public static void mostrarMenu() {
        // Muestra por pantalla el menú principal con todas las opciones disponibles.
        //Salto de línea vacío
        System.out.println();
        System.out.println("===== BIBLIOTECA =====");
        //Recupera todas las opciones definidas en el enum Opcion
        Opcion[] opcion = Opcion.values();
        //Recorre el array y muestra cada opción como: índice - NOMBRE BONITO
        //sustituye '_' por espacios para que se lea mejor).
        for (int i = 0; i < opcion.length; i++) {
            System.out.println(i + " - " + opcion[i].name().replace('_', ' '));
        }
        System.out.println("======================");
    }

    public static Usuario nuevoUsuario(boolean paraBuscar) {
        // Si paraBuscar es true, solo pide el DNI y crea un "usuario clave" para buscar.
        // Si paraBuscar es false, pide todos los datos y crea un usuario completo.
        Usuario usuario = null;

        // Mientras no se haya creado el usuario, seguimos pidiendo datos.
        while (usuario == null) {
            try {
                System.out.print("DNI: ");
                String dni = Entrada.cadena().trim();

                // //Usuario incompleto para poder pasarlo a método de busca.
                // Se crea un objeto dirección vacío para pasárselo al usuario incompleto.
                if (paraBuscar) {
                    Direccion d = new Direccion("", "", "", "");
                    usuario = new Usuario(dni, "", "", d);
                } else {
                    System.out.print("Nombre: ");
                    String nombre = Entrada.cadena().trim();

                    System.out.print("Email: ");
                    String email = Entrada.cadena().trim();

                    System.out.print("Via: ");
                    String via = Entrada.cadena().trim();

                    System.out.print("Numero: ");
                    String numero = Entrada.cadena().trim();

                    System.out.print("CP: ");
                    String cp = Entrada.cadena().trim();

                    System.out.print("Localidad: ");
                    String localidad = Entrada.cadena().trim();

                    // Con los datos principales ya recogidos, intentamos construir el Libro.
                    // Si el constructor valida y falla, saltará al catch y repetiremos.

                    Direccion d = new Direccion(via, numero, cp, localidad);
                    usuario = new Usuario(dni, nombre, email, d);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Datos invalidos: " + e.getMessage());
            }
        }

        return usuario;
    }

    public static Libro nuevoLibro(boolean paraBuscar) {
        // Si paraBuscar es true, solo pide el ISBN y crea un "libro clave" para buscar.
        // Si paraBuscar es false, pide todos los datos y añade los autores.
        Libro libro = null;

        // Mientras no se haya creado el libro, seguimos pidiendo datos.
        while (libro == null) {
            try {
                System.out.print("ISBN: ");
                String isbn = Entrada.cadena().trim();

                //Libro incompleto para poder pasarlo a método de busca.
                if (paraBuscar) {
                    libro = new Libro(isbn, "", 0, Categoria.OTROS);
                } else {
                    // Elegimos el tipo de libro a crear.
                    int tipo = 0;
                    while (tipo != 1 && tipo != 2) {
                        System.out.println("Tipo de material:");
                        System.out.println("1) Libro");
                        System.out.println("2) Audiolibro");
                        System.out.print("Opcion: ");
                        tipo = Entrada.entero();
                        if (tipo != 1 && tipo != 2) {
                            System.out.println("Opcion invalida.");
                        }
                    }

                    System.out.print("Titulo: ");
                    String titulo = Entrada.cadena().trim();

                    System.out.print("Año: ");
                    int anio = Entrada.entero();
                    //     Mostramos el menú con todas las categorías del enum.
                    //     Repetimos hasta que el usuario elija una opción válida.
                    Categoria categoria = null;
                    while (categoria == null) {
                        Categoria[] cs = Categoria.values();
                        System.out.println("Categoria:");
                        for (int i = 0; i < cs.length; i++) {
                            System.out.println((i + 1) + ") " + cs[i]);
                        }
                        System.out.print("Opcion: ");
                        int op = Entrada.entero();
                        // Si la opción está en rango, asignamos categoría.
                        // Si no, mostramos error y repetimos el menú.
                        if (op >= 1 && op <= cs.length) {
                            categoria = cs[op - 1];
                        } else {
                            System.out.println("Opcion invalida.");
                        }
                    }


                    //Con los datos principales ya recogidos, intentamos construir el Libro o audiolibro.
                    //Si el constructor valida y falla, saltará al catch y repetiremos.
                    if (tipo == 1) {
                        libro = new Libro(isbn, titulo, anio, categoria);
                    } else {
                        // Audiolibro: pedimos duración en minutos y formato.
                        int minutos = 0;
                        while (minutos <= 0) {
                            System.out.print("Duracion (en minutos, > 0): ");
                            minutos = Entrada.entero();
                            if (minutos <= 0) {
                                System.out.println("Duracion invalida.");
                            }
                        }
                        Duration duracion = Duration.ofMinutes(minutos);

                        //Pedimos el formato. Mientras la cadena esté vacía lo seguimos pidiendo.
                        String formato = "";
                        while (formato.isBlank()) {
                            System.out.print("Formato (ej: MP3, MP4B, AA/AAX...): ");
                            formato = Entrada.cadena().trim();
                            if (formato.isBlank()) {
                                System.out.println("Formato invalido.");
                            }
                        }

                        libro = new Audiolibro(isbn, titulo, anio, categoria, duracion, formato);
                    }
                    //Pedimos el número de autores
                    int nAutores = 0;
                    while (nAutores < 1) {
                        System.out.print("Numero de autores (minimo 1): ");
                        nAutores = Entrada.entero();
                        if (nAutores < 1) {
                            System.out.println("Numero invalido. Debe ser al menos 1.");
                        }
                    }
                    // Por cada autor, llamamos a nuevoAutor() y lo añadimos al libro.
                    for (int i = 0; i < nAutores; i++) {
                        Autor a = nuevoAutor();
                        libro.addAutor(a);
                    }
                }
                    //Si addAutor valida y falla, podría lanzar excepción y volveríamos al bucle externo.
                } catch(IllegalArgumentException e){ // Formato incorrecto, valores fuera de rango, ISBN vacío, etc.
                    System.out.println("Datos invalidos: " + e.getMessage());
                } catch(IllegalStateException e){ // El estado del objeto no permite crearlo
                    // o algún método interno impide completar la operación.
                    System.out.println("No se pudo crear el libro: " + e.getMessage());
                }
            }
            //Cuando conseguimos construir un Libro válido, salimos del bucle y lo devolvemos.
            return libro;
    }

    public static Autor nuevoAutor() {
        // Pide por teclado los datos del autor y devuelve el objeto creado.
        // Repite el proceso hasta que el constructor de Autor acepte los datos (autor != null).
        Autor autor = null;

        while (autor == null) {
            try {
                System.out.print("Autor - Nombre: ");
                String nombre = Entrada.cadena().trim();

                System.out.print("Autor - Apellidos: ");
                String apellidos = Entrada.cadena().trim();

                System.out.print("Autor - Nacionalidad: ");
                String nacionalidad = Entrada.cadena().trim();

                // Intenta crear el objeto; si hay validaciones internas y fallan,
                // el constructor lanzará IllegalArgumentException y se reintentará.
                autor = new Autor(nombre, apellidos, nacionalidad);
            } catch (IllegalArgumentException e) {
                System.out.println("Datos invalidos: " + e.getMessage());
            }
        }
        //Devuelve el autor ya creado.
        return autor;
    }

    public static LocalDate leerFecha() {
        // Devuelve la fecha actual del sistema.
        return LocalDate.now();
    }
}
