package com.example.policine.model.session;

import com.example.policine.model.entities.Funcion;
import com.example.policine.model.entities.Pelicula;
import com.example.policine.model.entities.Sala;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Singleton para manejar los datos de la sesión de reserva
 */
public class BookingSession {
    private static BookingSession instance;

    // Datos de la película y función
    private Pelicula pelicula;
    private Funcion funcion;
    private Sala sala;

    // Datos de asientos
    private Set<String> asientosSeleccionados;
    private Map<String, Double> preciosAsientos;

    // Datos de comida
    private Map<String, Integer> comidaSeleccionada;
    private Map<String, Double> preciosComida;

    // Datos de precios
    private double subtotalAsientos = 0.0;
    private double subtotalComida = 0.0;
    private double impuestos = 0.0;
    private double descuento = 0.0;
    private double total = 0.0;

    // Datos de entrega
    private boolean entregarEnAsiento = false;

    // Datos de sesión
    private LocalDateTime inicioSesion;
    private int idUsuario = 1; // Por defecto, deberías obtenerlo de la sesión de usuario

    private BookingSession() {
        reset();
    }

    public static BookingSession getInstance() {
        if (instance == null) {
            instance = new BookingSession();
        }
        return instance;
    }

    public void reset() {
        pelicula = null;
        funcion = null;
        sala = null;
        asientosSeleccionados = new HashSet<>();
        preciosAsientos = new HashMap<>();
        comidaSeleccionada = new HashMap<>();
        preciosComida = new HashMap<>();
        subtotalAsientos = 0.0;
        subtotalComida = 0.0;
        impuestos = 0.0;
        descuento = 0.0;
        total = 0.0;
        entregarEnAsiento = false;
        inicioSesion = LocalDateTime.now();
    }

    // Métodos para película y función
    public void setMovieData(Pelicula pelicula, Funcion funcion, Sala sala) {
        this.pelicula = pelicula;
        this.funcion = funcion;
        this.sala = sala;
        if (inicioSesion == null) {
            inicioSesion = LocalDateTime.now();
        }
    }

    // Métodos para asientos
    public void setSeatData(Set<String> asientos, Map<String, Double> precios) {
        this.asientosSeleccionados = new HashSet<>(asientos);
        this.preciosAsientos = new HashMap<>(precios);
        calculateSeatSubtotal();
    }

    // Métodos para comida
    public void setFoodData(Map<String, Integer> comida, Map<String, Double> precios) {
        this.comidaSeleccionada = new HashMap<>(comida);
        this.preciosComida = new HashMap<>(precios);
        calculateFoodSubtotal();
    }

    // Cálculos
    private void calculateSeatSubtotal() {
        subtotalAsientos = asientosSeleccionados.stream()
                .mapToDouble(asiento -> preciosAsientos.getOrDefault(asiento, 0.0))
                .sum();
        calculateTotal();
    }

    private void calculateFoodSubtotal() {
        subtotalComida = 0.0;
        for (Map.Entry<String, Integer> entry : comidaSeleccionada.entrySet()) {
            String producto = entry.getKey();
            int cantidad = entry.getValue();
            double precio = preciosComida.getOrDefault(producto, 0.0);
            subtotalComida += precio * cantidad;
        }
        calculateTotal();
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
        calculateTotal();
    }

    private void calculateTotal() {
        double subtotalGeneral = subtotalAsientos + subtotalComida;
        impuestos = subtotalGeneral * 0.08; // 8% de impuestos
        total = subtotalGeneral + impuestos - descuento;
    }

    // Métodos para verificar tiempo de sesión
    public boolean isSessionExpired() {
        if (inicioSesion == null) return true;
        return LocalDateTime.now().isAfter(inicioSesion.plusMinutes(10));
    }

    public long getSecondsUntilExpiration() {
        if (inicioSesion == null) return 0;
        LocalDateTime expiration = inicioSesion.plusMinutes(10);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiration)) return 0;
        return java.time.Duration.between(now, expiration).getSeconds();
    }

    // Getters
    public Pelicula getPelicula() { return pelicula; }
    public Funcion getFuncion() { return funcion; }
    public Sala getSala() { return sala; }
    public Set<String> getAsientosSeleccionados() { return new HashSet<>(asientosSeleccionados); }
    public Map<String, Double> getPreciosAsientos() { return new HashMap<>(preciosAsientos); }
    public Map<String, Integer> getComidaSeleccionada() { return new HashMap<>(comidaSeleccionada); }
    public Map<String, Double> getPreciosComida() { return new HashMap<>(preciosComida); }

    public double getSubtotalAsientos() { return subtotalAsientos; }
    public double getSubtotalComida() { return subtotalComida; }
    public double getSubtotalGeneral() { return subtotalAsientos + subtotalComida; }
    public double getImpuestos() { return impuestos; }
    public double getDescuento() { return descuento; }
    public double getTotal() { return total; }

    public boolean isEntregarEnAsiento() { return entregarEnAsiento; }
    public void setEntregarEnAsiento(boolean entregarEnAsiento) { this.entregarEnAsiento = entregarEnAsiento; }

    public LocalDateTime getInicioSesion() { return inicioSesion; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    // Método para obtener resumen completo
    public String getResumenCompleto() {
        StringBuilder resumen = new StringBuilder();

        if (pelicula != null) {
            resumen.append("Película: ").append(pelicula.getTitulo()).append("\n");
        }
        if (funcion != null && sala != null) {
            resumen.append("Función: ").append(funcion.getHora()).append(" - ").append(sala.getNombreSala()).append("\n");
            resumen.append("Fecha: ").append(funcion.getFecha()).append("\n");
        }

        if (!asientosSeleccionados.isEmpty()) {
            resumen.append("Asientos: ").append(String.join(", ", asientosSeleccionados)).append("\n");
        }

        if (!comidaSeleccionada.isEmpty()) {
            resumen.append("Comida:\n");
            for (Map.Entry<String, Integer> entry : comidaSeleccionada.entrySet()) {
                resumen.append("  - ").append(entry.getKey()).append(" x").append(entry.getValue()).append("\n");
            }
        }

        resumen.append("Entrega: ").append(entregarEnAsiento ? "En asiento" : "En taquilla").append("\n");
        resumen.append(String.format("Subtotal: $%.2f\n", getSubtotalGeneral()));
        resumen.append(String.format("Impuestos: $%.2f\n", impuestos));
        if (descuento > 0) {
            resumen.append(String.format("Descuento: -$%.2f\n", descuento));
        }
        resumen.append(String.format("Total: $%.2f", total));

        return resumen.toString();
    }
}