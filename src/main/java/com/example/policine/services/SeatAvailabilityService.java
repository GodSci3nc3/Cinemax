package com.example.policine.services;

import com.example.policine.model.dao.*;
import com.example.policine.model.entities.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio especializado para manejar la disponibilidad de asientos
 */
public class SeatAvailabilityService {

    private AsientoDAO asientoDAO;
    private ReservaAsientoDAO reservaAsientoDAO;
    private SalaDAO salaDAO;
    private FuncionDAO funcionDAO;

    public SeatAvailabilityService() {
        this.asientoDAO = new AsientoDAO();
        this.reservaAsientoDAO = new ReservaAsientoDAO();
        this.salaDAO = new SalaDAO();
        this.funcionDAO = new FuncionDAO();
    }

    /**
     * Obtener mapa completo de disponibilidad de asientos para una función
     */
    public SeatMap obtenerMapaAsientos(int idFuncion) {
        try {
            // Obtener función y sala
            Funcion funcion = funcionDAO.buscarPorId(idFuncion);
            if (funcion == null) {
                throw new IllegalArgumentException("Función no encontrada");
            }

            Sala sala = salaDAO.buscarPorId(funcion.getIdSala());
            if (sala == null) {
                throw new IllegalArgumentException("Sala no encontrada");
            }

            // Inicializar asientos si no existen
            BookingService bookingService = new BookingService();
            bookingService.inicializarAsientosSala(sala.getIdSala());

            // Obtener todos los asientos de la sala
            List<Asiento> todosAsientos = asientoDAO.listarPorSala(sala.getIdSala());

            // Obtener asientos ocupados
            List<String> asientosOcupados = reservaAsientoDAO.obtenerAsientosReservadosParaFuncion(idFuncion);
            Set<String> ocupadosSet = new HashSet<>(asientosOcupados);

            // Crear mapa de asientos
            Map<String, SeatInfo> mapaAsientos = new HashMap<>();

            for (Asiento asiento : todosAsientos) {
                String codigo = asiento.getCodigoAsiento();
                boolean disponible = !ocupadosSet.contains(codigo);

                SeatInfo seatInfo = new SeatInfo(
                        asiento.getIdAsiento(),
                        codigo,
                        asiento.getFila(),
                        asiento.getNumero(),
                        asiento.getTipo(),
                        disponible,
                        asiento.getPrecioBase()
                );

                mapaAsientos.put(codigo, seatInfo);
            }

            return new SeatMap(sala, mapaAsientos, asientosOcupados.size(), todosAsientos.size());

        } catch (Exception e) {
            System.err.println("Error al obtener mapa de asientos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Verificar si los asientos están disponibles y reservarlos temporalmente
     */
    public ReservationStatus verificarYReservarTemporalmente(int idFuncion, List<String> codigosAsientos, int idUsuario) {
        try {
            // Verificar disponibilidad actual
            List<String> asientosOcupados = reservaAsientoDAO.obtenerAsientosReservadosParaFuncion(idFuncion);

            List<String> noDisponibles = new ArrayList<>();
            for (String codigo : codigosAsientos) {
                if (asientosOcupados.contains(codigo)) {
                    noDisponibles.add(codigo);
                }
            }

            if (!noDisponibles.isEmpty()) {
                return new ReservationStatus(false, "Asientos no disponibles: " + String.join(", ", noDisponibles), noDisponibles);
            }

            // TODO: Implementar reserva temporal (opcional, para evitar conflictos durante el proceso)
            // Esto requeriría una tabla adicional o un campo de estado en Reserva_Asiento

            return new ReservationStatus(true, "Asientos disponibles", new ArrayList<>());

        } catch (Exception e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
            return new ReservationStatus(false, "Error técnico: " + e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * Obtener estadísticas de ocupación de una función
     */
    public OccupancyStats obtenerEstadisticasOcupacion(int idFuncion) {
        try {
            Funcion funcion = funcionDAO.buscarPorId(idFuncion);
            if (funcion == null) return null;

            Sala sala = salaDAO.buscarPorId(funcion.getIdSala());
            if (sala == null) return null;

            int asientosOcupados = asientoDAO.contarAsientosOcupados(idFuncion);
            int capacidadTotal = sala.getCapacidad();
            int asientosDisponibles = capacidadTotal - asientosOcupados;
            double porcentajeOcupacion = (double) asientosOcupados / capacidadTotal * 100.0;

            return new OccupancyStats(
                    capacidadTotal,
                    asientosOcupados,
                    asientosDisponibles,
                    porcentajeOcupacion,
                    asientosDisponibles == 0 // está llena
            );

        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtener recomendaciones de asientos
     */
    public List<String> recomendarAsientos(int idFuncion, int cantidadAsientos) {
        List<String> recomendaciones = new ArrayList<>();

        try {
            SeatMap mapa = obtenerMapaAsientos(idFuncion);
            if (mapa == null) return recomendaciones;

            // Filtrar asientos disponibles
            List<SeatInfo> disponibles = mapa.getMapaAsientos().values().stream()
                    .filter(SeatInfo::isDisponible)
                    .sorted((a, b) -> {
                        // Priorizar filas del medio y asientos contiguos
                        int filaDiff = Math.abs(a.getFila() - (mapa.getFilaMaxima() / 2));
                        int filaDiffB = Math.abs(b.getFila() - (mapa.getFilaMaxima() / 2));
                        if (filaDiff != filaDiffB) {
                            return filaDiff - filaDiffB;
                        }
                        return a.getNumero() - b.getNumero();
                    })
                    .collect(Collectors.toList());

            // Buscar asientos contiguos
            if (cantidadAsientos > 1) {
                recomendaciones = buscarAsientosContiguos(disponibles, cantidadAsientos);
            }

            // Si no se pueden encontrar contiguos, tomar los mejores disponibles
            if (recomendaciones.isEmpty()) {
                recomendaciones = disponibles.stream()
                        .limit(cantidadAsientos)
                        .map(SeatInfo::getCodigo)
                        .collect(Collectors.toList());
            }

        } catch (Exception e) {
            System.err.println("Error al recomendar asientos: " + e.getMessage());
        }

        return recomendaciones;
    }

    private List<String> buscarAsientosContiguos(List<SeatInfo> disponibles, int cantidad) {
        Map<Integer, List<SeatInfo>> porFila = disponibles.stream()
                .collect(Collectors.groupingBy(SeatInfo::getFila));

        for (List<SeatInfo> asientosFila : porFila.values()) {
            asientosFila.sort((a, b) -> a.getNumero() - b.getNumero());

            for (int i = 0; i <= asientosFila.size() - cantidad; i++) {
                boolean sonContiguos = true;

                for (int j = 0; j < cantidad - 1; j++) {
                    if (asientosFila.get(i + j).getNumero() + 1 != asientosFila.get(i + j + 1).getNumero()) {
                        sonContiguos = false;
                        break;
                    }
                }

                if (sonContiguos) {
                    return asientosFila.subList(i, i + cantidad).stream()
                            .map(SeatInfo::getCodigo)
                            .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }

    // Clases internas para datos estructurados

    public static class SeatMap {
        private final Sala sala;
        private final Map<String, SeatInfo> mapaAsientos;
        private final int asientosOcupados;
        private final int capacidadTotal;

        public SeatMap(Sala sala, Map<String, SeatInfo> mapaAsientos, int asientosOcupados, int capacidadTotal) {
            this.sala = sala;
            this.mapaAsientos = mapaAsientos;
            this.asientosOcupados = asientosOcupados;
            this.capacidadTotal = capacidadTotal;
        }

        public int getFilaMaxima() {
            return mapaAsientos.values().stream()
                    .mapToInt(SeatInfo::getFila)
                    .max()
                    .orElse(0);
        }

        public int getNumeroMaximo() {
            return mapaAsientos.values().stream()
                    .mapToInt(SeatInfo::getNumero)
                    .max()
                    .orElse(0);
        }

        // Getters
        public Sala getSala() { return sala; }
        public Map<String, SeatInfo> getMapaAsientos() { return mapaAsientos; }
        public int getAsientosOcupados() { return asientosOcupados; }
        public int getCapacidadTotal() { return capacidadTotal; }
        public int getAsientosDisponibles() { return capacidadTotal - asientosOcupados; }
        public double getPorcentajeOcupacion() { return (double) asientosOcupados / capacidadTotal * 100.0; }
        public boolean estaLlena() { return asientosOcupados >= capacidadTotal; }
    }

    public static class SeatInfo {
        private final int idAsiento;
        private final String codigo;
        private final int fila;
        private final int numero;
        private final String tipo;
        private final boolean disponible;
        private final double precio;

        public SeatInfo(int idAsiento, String codigo, int fila, int numero, String tipo, boolean disponible, double precio) {
            this.idAsiento = idAsiento;
            this.codigo = codigo;
            this.fila = fila;
            this.numero = numero;
            this.tipo = tipo;
            this.disponible = disponible;
            this.precio = precio;
        }

        // Getters
        public int getIdAsiento() { return idAsiento; }
        public String getCodigo() { return codigo; }
        public int getFila() { return fila; }
        public int getNumero() { return numero; }
        public String getTipo() { return tipo; }
        public boolean isDisponible() { return disponible; }
        public double getPrecio() { return precio; }
    }

    public static class ReservationStatus {
        private final boolean exitoso;
        private final String mensaje;
        private final List<String> asientosNoDisponibles;

        public ReservationStatus(boolean exitoso, String mensaje, List<String> asientosNoDisponibles) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.asientosNoDisponibles = asientosNoDisponibles;
        }

        // Getters
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public List<String> getAsientosNoDisponibles() { return asientosNoDisponibles; }
    }

    public static class OccupancyStats {
        private final int capacidadTotal;
        private final int asientosOcupados;
        private final int asientosDisponibles;
        private final double porcentajeOcupacion;
        private final boolean estaLlena;

        public OccupancyStats(int capacidadTotal, int asientosOcupados, int asientosDisponibles, double porcentajeOcupacion, boolean estaLlena) {
            this.capacidadTotal = capacidadTotal;
            this.asientosOcupados = asientosOcupados;
            this.asientosDisponibles = asientosDisponibles;
            this.porcentajeOcupacion = porcentajeOcupacion;
            this.estaLlena = estaLlena;
        }

        // Getters
        public int getCapacidadTotal() { return capacidadTotal; }
        public int getAsientosOcupados() { return asientosOcupados; }
        public int getAsientosDisponibles() { return asientosDisponibles; }
        public double getPorcentajeOcupacion() { return porcentajeOcupacion; }
        public boolean isEstaLlena() { return estaLlena; }
    }
}