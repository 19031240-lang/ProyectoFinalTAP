package org.bibliotecafxv.model;

public class Libro {
    private int id;
    private String titulo;
    private int autorId;
    private int categoriaId;
    private String autor;
    private String categoria;
    private boolean disponible;
    private String descripcion;
    private String portada;
    private int idAutor;
    private int idCategoria;


    public Libro() {}

    // Constructor
    public Libro(int id, String titulo, int autorId, int categoriaId, String autor, String categoria, boolean disponible, String descripcion, String portada) {
        this.id = id;
        this.titulo = titulo;
        this.autorId = autorId;
        this.categoriaId = categoriaId;
        this.autor = autor;
        this.categoria = categoria;
        this.disponible = disponible;
        this.descripcion = descripcion;
        this.portada = portada;
    }


    // --- GETTERS Y SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getAutorId() { return autorId; }
    public void setAutorId(int autorId) { this.autorId = autorId; }

    public int getCategoriaId() { return categoriaId; }
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPortada() { return portada; }
    public void setPortada(String portada) { this.portada = portada; }

    public int getIdAutor() { return idAutor; }

    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }

    public int getIdCategoria() { return idCategoria; }

    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    @Override
    public String toString() { return this.titulo; }
}