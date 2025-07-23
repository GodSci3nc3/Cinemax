package com.example.policine.model.entities;

public class Sala {
    private int idSala;
    private String nombreSala;
    private int capacidad;
    private String tipo;
    private int idCine;

    // Constructor vacío
    public Sala() {}

    // Constructor completo
    public Sala(int idSala, String nombreSala, int capacidad, String tipo, int idCine) {
        this.idSala = idSala;
        this.nombreSala = nombreSala;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.idCine = idCine;
    }

    // Constructor sin ID (para insertar)
    public Sala(String nombreSala, int capacidad, String tipo, int idCine) {
        this.nombreSala = nombreSala;
        this.capacidad = capacidad;
        this.tipo = tipo;
        this.idCine = idCine;
    }

    // Getters y Setters
    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getIdCine() {
        return idCine;
    }

    public void setIdCine(int idCine) {
        this.idCine = idCine;
    }

    @Override
    public String toString() {
        return "Sala{" +
                "idSala=" + idSala +
                ", nombreSala='" + nombreSala + '\'' +
                ", capacidad=" + capacidad +
                ", tipo='" + tipo + '\'' +
                ", idCine=" + idCine +
                '}';
    }
}