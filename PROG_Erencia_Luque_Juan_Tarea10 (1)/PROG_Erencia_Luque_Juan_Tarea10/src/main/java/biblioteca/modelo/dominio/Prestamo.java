package biblioteca.modelo.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*
  Prestamo:
  - Guarda Libro y Usuario como copias para evitar aliasing.
  - Calcula fecha limite automaticamente sumando 15 dias.
*/
public class Prestamo {

    private Libro libro;
    private Usuario usuario;
    private LocalDate finicio;
    private LocalDate fLimite;
    private boolean devuelto;
    private LocalDate fDevolucion;

    public Prestamo(Libro libro, Usuario usuario, LocalDate finicio) {
        setLibro(libro);
        setUsuario(usuario);
        setFinicio(finicio);
        this.fLimite = finicio.plusDays(15);
        this.devuelto = false;
        this.fDevolucion = null;
    }

    public Libro getLibro() {
        return copiarLibro(libro);
    }

    public void setLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Libro nulo.");
        }
        this.libro = copiarLibro(libro);
    }

    public void setUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario nulo.");
        }
        this.usuario = new Usuario(usuario);
    }

    public Usuario getUsuario() {
        return new Usuario(usuario);
    }

    public LocalDate getFinicio() {
        return finicio;
    }

    public void setFinicio(LocalDate finicio) {
        if (finicio == null) {
            throw new IllegalArgumentException("Fecha nula.");
        }
        this.finicio = finicio;
    }

    public LocalDate getfLimite() {
        return fLimite;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public LocalDate getfDevolucion() {
        return fDevolucion;
    }

    public int diasRetraso() {
        LocalDate referencia = devuelto ? fDevolucion : LocalDate.now();

        if (!referencia.isAfter(fLimite)) {
            return 0;
        }

        return (int) ChronoUnit.DAYS.between(fLimite, referencia);
    }

    public boolean estaVencido() {
        return !devuelto && LocalDate.now().isAfter(fLimite);
    }

    public void marcarDevuelto(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Fecha devolucion nula.");
        }
        devuelto = true;
        fDevolucion = fecha;
    }

    private Libro copiarLibro(Libro libro) {
        if (libro instanceof Audiolibro) {
            return new Audiolibro((Audiolibro) libro);
        }
        return new Libro(libro);
    }

    @Override
    public String toString() {
        return "Prestamo{libro=" + libro + ", usuario=" + usuario
                + ", FechaInicio=" + finicio + ", FechaLimite=" + fLimite
                + ", Devuelto=" + devuelto + ", FechaDevolucion=" + fDevolucion
                + ", DiasDeRetraso=" + diasRetraso() + "}";
    }
}
