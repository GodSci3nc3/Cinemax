package com.example.policine.services;

import com.example.policine.model.dao.*;
import com.example.policine.model.entities.*;
import com.example.policine.model.session.BookingSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BookingService {

    private ReservaDAO reservaDAO;
    private AsientoDAO asientoDAO;
    private ReservaAsientoDAO reservaAsientoDAO;
    private FuncionDAO funcionDAO;
    private SalaDAO salaDAO;

    public BookingService() {
        this.reservaDAO = new ReservaDAO();
        this.asientoDAO = new AsientoDAO();
        this.reservaAsientoDAO = new ReservaAsientoDAO();
        this.funcionDAO = new FuncionDAO();
        this.salaDAO = new SalaDAO();
    }

    public ReservaCompleta procesarReserva(BookingSession session) throws ReservaException {
        if (session.getFuncion() == null || session.getAsientosSeleccionados().isEmpty()) {
            throw new ReservaException("Datos de sesión incompletos");
        }

        if (!verificarDisponibilidadAsientos(session.getFuncion().getIdFuncion(),
                session.getAsientosSeleccionados())) {
            throw new ReservaException("Algunos asientos ya no están disponibles");
        }

        try {
            Reserva nuevaReserva = new Reserva(
                    LocalDateTime.now(),
                    "Confirmada",
                    session.getIdUsuario(),
                    session.getFuncion().getIdFuncion()
            );

            boolean reservaGuardada = reservaDAO.insertar(nuevaReserva);
            if (!reservaGuardada) {
                throw new ReservaException("Error al guardar la reserva");
            }

            List<Integer> idsAsientos = obtenerIdsAsientos(
                    session.getFuncion().getIdFuncion(),
                    session.getAsientosSeleccionados()
            );

            if (idsAsientos.size() != session.getAsientosSeleccionados().size()) {
                throw new ReservaException("No se pudieron encontrar todos los asientos");
            }

            boolean asientosGuardados = reservaAsientoDAO.insertarMultiples(
                    nuevaReserva.getIdReserva(),
                    idsAsientos
            );

            if (!asientosGuardados) {
                reservaDAO.eliminar(nuevaReserva.getIdReserva());
                throw new ReservaException("Error al reservar los asientos");
            }

            return new ReservaCompleta(
                    nuevaReserva,
                    idsAsientos,
                    session.getAsientosSeleccionados(),
                    session.getTotal()
            );

        } catch (Exception e) {
            throw new ReservaException("Error al procesar la reserva: " + e.getMessage());
        }
    }

    public boolean verificarDisponibilidadAsientos(int idFuncion, Set<String> codigosAsientos) {
        try {
            List<String> asientosOcupados = reservaAsientoDAO.obtenerAsientosReservadosParaFuncion(idFuncion);

            for (String codigo : codigosAsientos) {
                if (asientosOcupados.contains(codigo)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
            return false;
        }
    }

    public List<Asiento> obtenerAsientosDisponibles(int idFuncion, int idSala) {
        return asientoDAO.obtenerAsientosDisponibles(idFuncion, idSala);
    }

    public List<String> obtenerAsientosOcupados(int idFuncion) {
        return reservaAsientoDAO.obtenerAsientosReservadosParaFuncion(idFuncion);
    }

    public boolean estaFuncionLlena(int idFuncion, int idSala) {
        try {
            Sala sala = salaDAO.buscarPorId(idSala);
            if (sala == null) return true;

            int asientosOcupados = asientoDAO.contarAsientosOcupados(idFuncion);
            return asientosOcupados >= sala.getCapacidad();

        } catch (Exception e) {
            System.err.println("Error al verificar si la función está llena: " + e.getMessage());
            return true;
        }
    }

    public double obtenerPorcentajeOcupacion(int idFuncion, int idSala) {
        try {
            Sala sala = salaDAO.buscarPorId(idSala);
            if (sala == null) return 100.0;

            int asientosOcupados = asientoDAO.contarAsientosOcupados(idFuncion);
            return (double) asientosOcupados / sala.getCapacidad() * 100.0;

        } catch (Exception e) {
            System.err.println("Error al calcular porcentaje de ocupación: " + e.getMessage());
            return 100.0;
        }
    }

    // 🔹 Método modificado para usar búsqueda tolerante
    private List<Integer> obtenerIdsAsientos(int idFuncion, Set<String> codigosAsientos) {
        List<Integer> ids = new ArrayList<>();
        Funcion funcion = funcionDAO.buscarPorId(idFuncion);
        if (funcion == null) return ids;

        for (String codigo : codigosAsientos) {
            Asiento asiento = asientoDAO.buscarPorCodigo(funcion.getIdSala(), codigo);
            if (asiento != null) {
                ids.add(asiento.getIdAsiento());
            } else {
                System.err.println("[Reserva] No se encontró en BD el asiento con código: " + codigo +
                        " (sala " + funcion.getIdSala() + ")");
            }
        }
        return ids;
    }



    public DetalleReserva obtenerDetalleReserva(int idReserva) {
        try {
            Reserva reserva = reservaDAO.buscarPorId(idReserva);
            if (reserva == null) return null;

            List<Integer> idsAsientos = reservaAsientoDAO.obtenerAsientosPorReserva(idReserva);
            List<Asiento> asientos = new ArrayList<>();

            for (int idAsiento : idsAsientos) {
                Asiento asiento = asientoDAO.buscarPorId(idAsiento);
                if (asiento != null) {
                    asientos.add(asiento);
                }
            }
            return new DetalleReserva(reserva, asientos);

        } catch (Exception e) {
            System.err.println("Error al obtener detalle de reserva: " + e.getMessage());
            return null;
        }
    }

    public boolean inicializarAsientosSala(int idSala) {
        try {
            Sala sala = salaDAO.buscarPorId(idSala);
            if (sala == null) return false;

            List<Asiento> asientosExistentes = asientoDAO.listarPorSala(idSala);
            if (!asientosExistentes.isEmpty()) return true;

            int filas, asientosPorFila;
            String tipoAsiento;

            switch (sala.getTipo().toLowerCase()) {
                case "vip":
                    filas = 5;
                    asientosPorFila = 16;
                    tipoAsiento = "VIP";
                    break;
                case "premium":
                    filas = 8;
                    asientosPorFila = 25;
                    tipoAsiento = "Premium";
                    break;
                default:
                    filas = 10;
                    asientosPorFila = 15;
                    tipoAsiento = "Estándar";
                    break;
            }
            return asientoDAO.generarAsientosPorSala(idSala, filas, asientosPorFila, tipoAsiento);

        } catch (Exception e) {
            System.err.println("Error al inicializar asientos: " + e.getMessage());
            return false;
        }
    }

    public static class ReservaCompleta {
        private final Reserva reserva;
        private final List<Integer> idsAsientos;
        private final Set<String> codigosAsientos;
        private final double total;

        public ReservaCompleta(Reserva reserva, List<Integer> idsAsientos,
                               Set<String> codigosAsientos, double total) {
            this.reserva = reserva;
            this.idsAsientos = idsAsientos;
            this.codigosAsientos = codigosAsientos;
            this.total = total;
        }

        public Reserva getReserva() { return reserva; }
        public List<Integer> getIdsAsientos() { return idsAsientos; }
        public Set<String> getCodigosAsientos() { return codigosAsientos; }
        public double getTotal() { return total; }
    }

    public static class DetalleReserva {
        private final Reserva reserva;
        private final List<Asiento> asientos;

        public DetalleReserva(Reserva reserva, List<Asiento> asientos) {
            this.reserva = reserva;
            this.asientos = asientos;
        }

        public Reserva getReserva() { return reserva; }
        public List<Asiento> getAsientos() { return asientos; }
    }

    public static class ReservaException extends Exception {
        public ReservaException(String message) {
            super(message);
        }
    }
}
