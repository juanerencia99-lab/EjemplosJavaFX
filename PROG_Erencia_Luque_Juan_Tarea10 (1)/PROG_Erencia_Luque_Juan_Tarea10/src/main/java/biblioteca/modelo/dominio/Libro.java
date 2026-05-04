package biblioteca.modelo.dominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
  Libro:
  - ISBN es el identificador unico (equals/hashCode por ISBN).
  - Un libro puede tener cualquier numero de autores.
  - El constructor copia crea una copia profunda de los autores.
*/
public class Libro implements Comparable<Libro> {

    public static final String ISBN_PATTERN = "\\d{3}-\\d{2}-\\d{5}-\\d{2}-\\d";

    private String isbn;
    private String titulo;
    private int anio;
    private Categoria categoria;
    private List<Autor> autores;

    public Libro(String isbn, String titulo, int anio, Categoria categoria) {
        autores = new ArrayList<>();

        setIsbn(isbn);
        setTitulo(titulo);
        setAnio(anio);
        setCategoria(categoria);
    }

    public Libro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo.");
        }

        autores = new ArrayList<>();

        setIsbn(libro.getIsbn());
        setTitulo(libro.getTitulo());
        setAnio(libro.getAnio());
        setCategoria(libro.getCategoria());

        for (Autor autor : libro.autores) {
            addAutor(autor);
        }
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        if (isbn == null) {
            throw new IllegalArgumentException("El ISBN no puede ser nulo.");
        }
        if (!isbn.matches(ISBN_PATTERN)) {
            throw new IllegalArgumentException("ISBN invalido. El formato debe ser 123-45-67890-12-1");
        }
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null) {
            throw new IllegalArgumentException("El titulo no puede ser nulo.");
        }
        this.titulo = titulo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        if ((anio < 0) || (anio > 2026)) {
            throw new IllegalArgumentException("El anio es invalido.");
        }
        this.anio = anio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        if (categoria == null) {
            categoria = Categoria.OTROS;
        }
        this.categoria = categoria;
    }

    public void addAutor(Autor a) {
        if (a == null) {
            throw new IllegalArgumentException("El autor no puede ser nulo.");
        }

        autores.add(new Autor(a));
    }

    public List<Autor> getAutores() {
        List<Autor> copiaAutores = new ArrayList<>();

        for (Autor autor : autores) {
            copiaAutores.add(new Autor(autor));
        }

        return copiaAutores;
    }

    private String autoresComoCadena() {
        String cadena = "";

        for (int i = 0; i < autores.size(); i++) {
            cadena += autores.get(i).getNombreCompleto();
            if (i < autores.size() - 1) {
                cadena += ", ";
            }
        }

        if (cadena.isEmpty()) {
            cadena = "(sin autores)";
        }

        return cadena;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Libro)) {
            return false;
        }
        Libro libro = (Libro) o;
        return Objects.equals(getIsbn(), libro.getIsbn());
    }

    @Override
    public int hashCode() {
        return isbn.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "{ISBN='" + isbn + "', Titulo='" + titulo + "', Anio=" + anio
                + ", NAutores=" + autores.size() + ", Categoria=" + categoria
                + ", Autores=" + autoresComoCadena() + "}";
    }

    @Override
    public int compareTo(Libro o) {
        int compare = this.titulo.compareToIgnoreCase(o.titulo);
        if (compare != 0) {
            return compare;
        }
        return this.isbn.compareToIgnoreCase(o.isbn);
    }
}
