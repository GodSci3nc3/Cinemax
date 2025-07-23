package com.example.policine.model.entities;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private String genero;
    private String clasificacion;
    private int duracion;
    private String idioma;
    private String sinopsis;

    // Constructor vacío
    public Pelicula() {}

    // Constructor completo
    public Pelicula(int idPelicula, String titulo, String genero, String clasificacion,
                    int duracion, String idioma, String sinopsis) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacion = clasificacion;
        this.duracion = duracion;
        this.idioma = idioma;
        this.sinopsis = sinopsis;
    }

    // Constructor sin ID (para insertar)
    public Pelicula(String titulo, String genero, String clasificacion,
                    int duracion, String idioma, String sinopsis) {
        this.titulo = titulo;
        this.genero = genero;
        this.clasificacion = clasificacion;
        this.duracion = duracion;
        this.idioma = idioma;
        this.sinopsis = sinopsis;
    }

    // Getters y Setters
    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "idPelicula=" + idPelicula +
                ", titulo='" + titulo + '\'' +
                ", genero='" + genero + '\'' +
                ", clasificacion='" + clasificacion + '\'' +
                ", duracion=" + duracion +
                ", idioma='" + idioma + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                '}';
    }
}