package com.example.policine.model.entities;

public class Cine {
    private int idCine;
    private String nombre;
    private String direccion;
    private String ciudad;

    // Constructor vacío
    public Cine() {}

    // Constructor completo
    public Cine(int idCine, String nombre, String direccion, String ciudad) {
        this.idCine = idCine;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    // Constructor sin ID (para insertar)
    public Cine(String nombre, String direccion, String ciudad) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    // Getters y Setters
    public int getIdCine() {
        return idCine;
    }

    public void setIdCine(int idCine) {
        this.idCine = idCine;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public String toString() {
        return "Cine{" +
                "idCine=" + idCine +
                ", nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", ciudad='" + ciudad + '\'' +
                '}';
    }
}