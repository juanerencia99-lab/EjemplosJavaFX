package biblioteca.modelo.dominio;

/*
  Dirección:
  CP_PATTERN valida el código postal (5 dígitos.).
  Se valida en el setter de CP.
*/
public class Direccion {

    public static final String CP_PATTERN = "\\d{5}";

    private String via;
    private String numero;
    private String cp;
    private String localidad;

    public Direccion(String via, String numero, String cp, String localidad) {
        // Crea la dirección usando setters para validar el CP.
        setVia(via);
        setNumero(numero);
        setCp(cp);
        setLocalidad(localidad);
    }

    public String getVia() {
        // Devuelve la vía (calle, avenida, etc.).
        return via;
    }

    public void setVia(String via) {
        // Asigna la vía (no puede ser null).
        if (via == null) {
            throw new IllegalArgumentException("La via no puede ser nula.");
        }
        this.via = via;
    }

    public String getNumero() {
        // Devuelve el número de la via.
        return numero;
    }

    public void setNumero(String numero) {
        // Asigna el número (no puede ser null).
        if (numero == null) {
            throw new IllegalArgumentException("El número no puede ser nulo.");
        }
        this.numero = numero;
    }

    public String getCp() {
        // Devuelve el código postal.
        return cp;
    }

    public void setCp(String cp) {
        // Asigna el código postal. Si no está vacío, debe cumplir CP_PATTERN.
        if (cp == null) {
            throw new IllegalArgumentException("El CP no puede ser nulo.");
        }
        // Permitimos "" para objetos "clave" (búsquedas)
        if (!cp.isEmpty() && !cp.matches(CP_PATTERN)) {
            throw new IllegalArgumentException("CP invalido.");
        }
        this.cp = cp;
    }

    public String getLocalidad() {
        // Devuelve la localidad.
        return localidad;
    }

    public void setLocalidad(String localidad) {
        // Asigna la localidad (no puede ser null).
        if (localidad == null) {
            throw new IllegalArgumentException("La localidad no puede ser nula.");
        }
        this.localidad = localidad;
    }

    @Override
    public String toString() {
        // Devuelve una representación legible de la dirección.
        return "Dirección{Vía='" + via + "', Número='" + numero + "', CP='" + cp + "', Localidad='" + localidad + "'}";
    }
}
