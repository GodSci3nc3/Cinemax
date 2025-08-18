package com.example.policine.model.entities;

import java.time.LocalDateTime;

public class Reserva {
    private int idReserva;
    private LocalDateTime fechaReserva; // datetime en la BD
    private String estado;
    private int usuarioIdUsuario; // Usuario_ID_Usuario en la BD
    private int funcionIdFuncion; // Funcion_ID_Funcion en la BD

    // Constructor vacío
    public Reserva() {}

    // Constructor completo
    public Reserva(int idReserva, LocalDateTime fechaReserva, String estado, int usuarioIdUsuario, int funcionIdFuncion) {
        this.idReserva = idReserva;
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.usuarioIdUsuario = usuarioIdUsuario;
        this.funcionIdFuncion = funcionIdFuncion;
    }

    // Constructor sin ID (para insertar)
    public Reserva(LocalDateTime fechaReserva, String estado, int usuarioIdUsuario, int funcionIdFuncion) {
        this.fechaReserva = fechaReserva;
        this.estado = estado;
        this.usuarioIdUsuario = usuarioIdUsuario;
        this.funcionIdFuncion = funcionIdFuncion;
    }

    // Getters y Setters
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getUsuarioIdUsuario() {
        return usuarioIdUsuario;
    }

    public void setUsuarioIdUsuario(int usuarioIdUsuario) {
        this.usuarioIdUsuario = usuarioIdUsuario;
    }

    public int getFuncionIdFuncion() {
        return funcionIdFuncion;
    }

    public void setFuncionIdFuncion(int funcionIdFuncion) {
        this.funcionIdFuncion = funcionIdFuncion;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", fechaReserva=" + fechaReserva +
                ", estado='" + estado + '\'' +
                ", usuarioIdUsuario=" + usuarioIdUsuario +
                ", funcionIdFuncion=" + funcionIdFuncion +
                '}';
    }
}