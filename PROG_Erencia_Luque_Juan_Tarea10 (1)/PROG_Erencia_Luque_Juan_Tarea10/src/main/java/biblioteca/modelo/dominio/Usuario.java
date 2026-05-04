package biblioteca.modelo.dominio;

import java.util.Objects;

/*
  Usuario:
  - DNI es el identificador único (equals/hashCode por DNI).
  - Los setters validan los campos.
  - getDireccion y setDireccion hacen copias para evitar aliasing.
  - El constructor copia reutiliza getters/setters.
*/
public class Usuario implements Comparable<Usuario> {

    public static final String DNI_PATTERN = "\\d{8}[A-Za-z]";
    public static final String EMAIL_BASIC = "^.+@.+\\..+$";

    private String dni;
    private String nombre;
    private String email;
    private Direccion direccion;

    public Usuario(String dni, String nombre, String email, Direccion direccion) {
        // Crea un usuario usando setters.
        setDni(dni);
        setNombre(nombre);
        setEmail(email);
        setDireccion(direccion);
    }

    public Usuario(Usuario usuario) {
        // Constructor copia: copia profunda de usuario y direccion (evita aliasing).
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }
        setDni(usuario.getDni());
        setNombre(usuario.getNombre());
        setEmail(usuario.getEmail());
        setDireccion(usuario.getDireccion());
    }

    public String getDni() {
        // Devuelve el DNI del usuario.
        return dni;
    }

    public void setDni(String dni) {
        // Asigna el DNI validándolo con DNI_PATTERN.
        if (dni == null) {
            throw new IllegalArgumentException("DNI nulo.");
        }
        if (!dni.matches(DNI_PATTERN)) {
            throw new IllegalArgumentException("DNI inválido. Deben ser 8 dígitos y una letra.");
        }
        this.dni = dni;
    }

    public String getNombre() {
        // Devuelve el nombre del usuario.
        return nombre;
    }

    public void setNombre(String nombre) {
        // Asigna el nombre (no puede ser null).
        if (nombre == null) {
            throw new IllegalArgumentException("Nombre nulo.");
        }
        this.nombre = nombre;
    }

    public String getEmail() {
        // Devuelve el email.
        return email;
    }

    public void setEmail(String email) {
        // Asigna el email. Si no esta vacío, debe cumplir EMAIL_BASIC.
        if (email == null) {
            throw new IllegalArgumentException("Email nulo.");
        }
        // Permitimos "" para objetos "clave" (búsquedas)
        if (!email.isEmpty() && !email.matches(EMAIL_BASIC)) {
            throw new IllegalArgumentException("Email invalido.");
        }
        this.email = email;
    }

    public Direccion getDireccion() {
        // Devuelve una copia de la direccion para evitar aliasing.
        Direccion d = direccion;
        return new Direccion(d.getVia(), d.getNumero(), d.getCp(), d.getLocalidad());
    }

    public void setDireccion(Direccion direccion) {
        // Asigna una copia de la direccion para evitar aliasing.
        if (direccion == null) {
            throw new IllegalArgumentException("Dirección nula.");
        }
        this.direccion = new Direccion(direccion.getVia(), direccion.getNumero(), direccion.getCp(), direccion.getLocalidad());
    }

    @Override
    public String toString() {
        // Devuelve una representación legible del usuario.
        return "Usuario{DNI='" + dni + "', Nombre='" + nombre + "', Email='" + email + "', Dirección=" + direccion + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(getDni(), usuario.getDni());
    }

    @Override
    public int hashCode() {
        // Genera hash coherente con equals.
        return dni.toLowerCase().hashCode();
    }

    //Sobrescritura de compareTo comparando por nombre y en caso de dos nombres, desempatando por DNI, ignorando caps (IgnoreCase).
    @Override
    public int compareTo(Usuario o) {
        int compare = this.nombre.compareToIgnoreCase(o.nombre);
        if (compare != 0) return compare;
        // Desempate: DNI (coherente con equals)
        return this.dni.compareToIgnoreCase(o.dni);
    }
}


