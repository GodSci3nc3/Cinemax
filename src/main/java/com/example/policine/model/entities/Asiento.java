package com.example.policine.model.entities;

public class Asiento {
    private int idAsiento;
    private int fila;
    private int numero;
    private String tipo;
    private int salaIdSala;

    // Constructor vacío
    public Asiento() {}

    // Constructor completo
    public Asiento(int idAsiento, int fila, int numero, String tipo, int salaIdSala) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.tipo = tipo;
        this.salaIdSala = salaIdSala;
    }

    // Constructor sin ID (para insertar)
    public Asiento(int fila, int numero, String tipo, int salaIdSala) {
        this.fila = fila;
        this.numero = numero;
        this.tipo = tipo;
        this.salaIdSala = salaIdSala;
    }

    // Getters y Setters
    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getSalaIdSala() {
        return salaIdSala;
    }

    public void setSalaIdSala(int salaIdSala) {
        this.salaIdSala = salaIdSala;
    }

    /**
     * Método para obtener el código del asiento (ej: A1, B5)
     */
    public String getCodigoAsiento() {
        char letraFila = (char) ('A' + (fila - 1));
        return letraFila + String.valueOf(numero);
    }

    /**
     * Método para obtener el precio base según el tipo de asiento
     */
    public double getPrecioBase() {
        switch (tipo.toLowerCase()) {
            case "vip":
                return 150.0;
            case "premium":
                return 120.0;
            case "estándar":
            case "estandar":
                return 100.0;
            default:
                return 100.0;
        }
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "idAsiento=" + idAsiento +
                ", fila=" + fila +
                ", numero=" + numero +
                ", tipo='" + tipo + '\'' +
                ", salaIdSala=" + salaIdSala +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Asiento asiento = (Asiento) obj;
        return idAsiento == asiento.idAsiento;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(idAsiento);
    }

    public Asiento buscarPorCodigo(int idSala, String codigoRaw) {
        if (codigoRaw == null) return null;
        String codigo = codigoRaw.trim().toUpperCase().replace("-", "");

        if (codigo.length() < 2) return null;

        char letra = codigo.charAt(0);
        if (letra < 'A' || letra > 'Z') return null;

        try {
            int numero = Integer.parseInt(codigo.substring(1).replaceFirst("^0+", ""));
            if (numero <= 0) return null;

            int fila1Based = (letra - 'A') + 1;

            Asiento a = buscarPorPosicion(idSala, fila1Based, numero);
            if (a != null) return a;

            // Plan B si la BD tiene filas 0-based
            if (fila1Based - 1 >= 0) {
                a = buscarPorPosicion(idSala, fila1Based - 1, numero);
                if (a != null) return a;
            }
            return null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Método placeholder para buscar un asiento por sala, fila y número.
     * Este método debe ser reemplazado por la lógica real de acceso a datos.
     */
    public Asiento buscarPorPosicion(int idSala, int fila, int numero) {
        // Implementación vacía, para que compile sin error.
        // La lógica real debe ir en el DAO, pero aquí se define para evitar errores de compilación.
        return null;
    }
}
