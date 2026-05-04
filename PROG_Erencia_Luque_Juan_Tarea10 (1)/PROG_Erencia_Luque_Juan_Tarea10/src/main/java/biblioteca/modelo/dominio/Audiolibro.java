package biblioteca.modelo.dominio;

import java.time.Duration;

public class Audiolibro extends Libro {

    private Duration duracion;
    private String formato;

    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        if (duracion == null) {
            throw new IllegalArgumentException("ERROR: La duración no puede ser nula");
        }
        this.duracion = duracion;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        if (formato == null) {
            throw new IllegalArgumentException("ERROR: El formato no puede ser nulo.");
        }
        this.formato = formato;
    }

    public Audiolibro(String isbn, String titulo, Integer anio, Categoria categoria, Duration duracion, String formato) {
        super(isbn, titulo, anio, categoria);
        setDuracion(duracion);
        setFormato(formato);
    }

    public Audiolibro(Audiolibro audiolibro) {
        super(audiolibro);
        setDuracion(audiolibro.getDuracion());
        setFormato(audiolibro.getFormato());
    }

    @Override
    public String toString() {
        long horas = duracion.toHours();
        int minutos = duracion.toMinutesPart();
        int segundos = duracion.toSecondsPart();

        // String.format hace la duración más legible con dos dígitos por componente.
        return "\nAudiolibro:"
                + super.toString()
                + String.format("Duración= %02d:%02d:%02d", horas, minutos, segundos)
                + ", formato='" + formato + '\'';
    }
}
