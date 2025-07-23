package com.example.policine.model.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Funcion {
    private int idFuncion;
    private LocalDate fecha;
    private LocalTime hora;
    private int idPelicula;
    private int idSala;

    // Constructor vacío
    public Funcion() {}

    // Constructor completo
    public Funcion(int idFuncion, LocalDate fecha, LocalTime hora, int idPelicula, int idSala) {
        this.idFuncion = idFuncion;
        this.fecha = fecha;
        this.hora = hora;
        this.idPelicula = idPelicula;
        this.idSala = idSala;
    }

    // Constructor sin ID (para insertar)
    public Funcion(LocalDate fecha, LocalTime hora, int idPelicula, int idSala) {
        this.fecha = fecha;
        this.hora = hora;
        this.idPelicula = idPelicula;
        this.idSala = idSala;
    }

    // Getters y Setters
    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    @Override
    public String toString() {
        return "Funcion{" +
                "idFuncion=" + idFuncion +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", idPelicula=" + idPelicula +
                ", idSala=" + idSala +
                '}';
    }
}