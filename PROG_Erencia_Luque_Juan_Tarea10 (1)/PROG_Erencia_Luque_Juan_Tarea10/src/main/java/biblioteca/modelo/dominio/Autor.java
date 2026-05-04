package biblioteca.modelo.dominio;

/*
  Autor:
  Representa la autoría de un libro.
  En el constructor se usan setters para validar.
  El constructor copia reutiliza el constructor principal (usa getters).
*/
public class Autor {

    private String nombre;
    private String apellidos;
    private String nacionalidad;

    public Autor(String nombre, String apellidos, String nacionalidad) {
        // Crea un autor usando setters
        setNombre(nombre);
        setApellidos(apellidos);
        setNacionalidad(nacionalidad);
    }

    public Autor(Autor autor) {
        // Constructor copia: crea un nuevo autor a partir de otro (evita aliasing).
        if (autor == null) {
            throw new IllegalArgumentException("No se puede crear un autor nulo.");
        }
        setNombre(autor.getNombre());
        setApellidos(autor.getApellidos());
        setNacionalidad(autor.getNacionalidad());
    }

    public String getNombre() {
        // Devuelve el nombre.
        return nombre;
    }

    public void setNombre(String nombre) {
        // Asigna el nombre (no puede ser null).
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre no puede ser  nulo.");
        }
        this.nombre = nombre;
    }

    public String getApellidos() {
        // Devuelve los apellidos.
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        // Asigna los apellidos (no puede ser null).
        if (apellidos == null) {
            throw new IllegalArgumentException("Los apellidos no pueden ser nulos.");
        }
        this.apellidos = apellidos;
    }

    public String getNacionalidad() {
        // Devuelve la nacionalidad.
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        // Asigna la nacionalidad (no puede ser null).
        if (nacionalidad == null) {
            throw new IllegalArgumentException("La nacionalidad no puede ser nula.");
        }
        this.nacionalidad = nacionalidad;
    }

    public String getNombreCompleto() {
        // Devuelve "nombre + apellidos" en una sola cadena.
        return (nombre + " " + apellidos).trim();
    }

    public String iniciales() {
        // Devuelve las iniciales del nombre y apellidos en mayúsculas.
        String ini = "";

        if (!nombre.isEmpty()) {
            ini += nombre.charAt(0);
        }
        if (!apellidos.isEmpty()) {
            ini += apellidos.charAt(0);
        }

        return ini.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        // Compara autores por nombre completo y nacionalidad.
        if (this == o) {
            return true;
        }
        if (!(o instanceof Autor)) {
            return false;
        }

        Autor a = (Autor) o;
        return getNombreCompleto().equalsIgnoreCase(a.getNombreCompleto());
    }

    @Override
    public int hashCode() {
        // Genera hash coherente con equals.
        return getNombreCompleto().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        // Devuelve una representación legible del autor.
        return "Autor{Nombre='" + nombre + "', Apellidos='" + apellidos + "', Nacionalidad='" + nacionalidad + "'}";
    }
}
