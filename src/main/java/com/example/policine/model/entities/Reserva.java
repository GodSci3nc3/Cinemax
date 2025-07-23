package com.example.policine.model.entities;

import java.time.LocalDate;

public class Reserva {
    private int idReserva;
    private LocalDate fechaReserva;
    private String estado;
    private int idUsuario;
    private int idFuncion;

    // Constructor vacío
    public Reserva() {}

    // Constructor completo
    public Reserva(int idReserva, LocalDate fechaReserva, String estado, int idUsuario, int idFuncion) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.idFuncion = idFuncion;
    }

    // Constructor sin ID (para insertar)
    public Reserva(LocalDate fechaReserva, String estado, int idUsuario, int idFuncion) {
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.idUsuario = idUsuario;
        this.idFuncion = idFuncion;
    }

    // Getters y Setters
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", fechaReserva=" + fechaReserva +
                ", estado='" + estado + '\'' +
                ", idUsuario=" + idUsuario +
                ", idFuncion=" + idFuncion +
                '}';
    }
}