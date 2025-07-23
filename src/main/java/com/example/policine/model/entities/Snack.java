package com.example.policine.model.entities;

public class Snack {
    private int idSnack;
    private String nombre;
    private double precio;
    private String descripcion;
    private String tipo;

    // Constructor vacío
    public Snack() {}

    // Constructor completo
    public Snack(int idSnack, String nombre, double precio, String descripcion, String tipo) {
        this.idSnack = idSnack;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    // Constructor sin ID (para insertar)
    public Snack(String nombre, double precio, String descripcion, String tipo) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    // Getters y Setters
    public int getIdSnack() {
        return idSnack;
    }

    public void setIdSnack(int idSnack) {
        this.idSnack = idSnack;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Snack{" +
                "idSnack=" + idSnack +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", descripcion='" + descripcion + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}