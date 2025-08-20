package com.example.policine.model.dao;

import com.example.policine.model.entities.Reserva;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public boolean insertar(Reserva reserva) {
        String sql = "INSERT INTO Reserva (Fecha_Reserva, Estado, Usuario_ID_Usuario, Funcion_ID_Funcion) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(reserva.getFechaReserva()));
            pstmt.setString(2, reserva.getEstado());
            pstmt.setInt(3, reserva.getUsuarioIdUsuario());
            pstmt.setInt(4, reserva.getFuncionIdFuncion());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    reserva.setIdReserva(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar reserva: " + e.getMessage());
        }
        return false;
    }

    public Reserva buscarPorId(int idReserva) {
        String sql = "SELECT * FROM Reserva WHERE ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getTimestamp("Fecha_Reserva").toLocalDateTime(),
                        rs.getString("Estado"),
                        rs.getInt("Usuario_ID_Usuario"),
                        rs.getInt("Funcion_ID_Funcion")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar reserva: " + e.getMessage());
        }
        return null;
    }

    public List<ReservaCompleta> listarReservasCompletas(int idUsuario) {
        List<ReservaCompleta> reservas = new ArrayList<>();
        String sql = """
            SELECT r.ID_Reserva, r.Fecha_Reserva, r.Estado,
                   p.Titulo as titulo_pelicula, p.Genero, p.Duracion, p.Clasificacion,
                   f.Fecha as fecha_funcion, f.Hora as hora_funcion,
                   s.Nombre_Sala as nombre_sala, s.Capacidad,
                   GROUP_CONCAT(CONCAT('Fila ', a.Fila, ' - Asiento ', a.Numero) SEPARATOR ', ') as asientos,
                   COUNT(ra.Asiento_ID_Asiento) as cantidad_asientos
            FROM Reserva r
            INNER JOIN Funcion f ON r.Funcion_ID_Funcion = f.ID_Funcion
            INNER JOIN Pelicula p ON f.Pelicula_ID_Pelicula = p.ID_Pelicula
            INNER JOIN Sala s ON f.Sala_ID_Sala = s.ID_Sala
            LEFT JOIN Reserva_Asiento ra ON r.ID_Reserva = ra.Reserva_ID_Reserva
            LEFT JOIN Asiento a ON ra.Asiento_ID_Asiento = a.ID_Asiento
            WHERE r.Usuario_ID_Usuario = ?
            GROUP BY r.ID_Reserva, r.Fecha_Reserva, r.Estado, p.Titulo, p.Genero, p.Duracion, p.Clasificacion,
                     f.Fecha, f.Hora, s.Nombre_Sala, s.Capacidad
            ORDER BY r.Fecha_Reserva DESC
            """;

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ReservaCompleta reservaCompleta = new ReservaCompleta();
                reservaCompleta.idReserva = rs.getInt("ID_Reserva");
                reservaCompleta.fechaReserva = rs.getTimestamp("Fecha_Reserva").toLocalDateTime();
                reservaCompleta.estado = rs.getString("Estado");
                reservaCompleta.tituloPelicula = rs.getString("titulo_pelicula");
                reservaCompleta.genero = rs.getString("Genero");
                reservaCompleta.duracion = rs.getInt("Duracion");
                reservaCompleta.clasificacion = rs.getString("Clasificacion");
                reservaCompleta.fechaFuncion = rs.getDate("fecha_funcion").toLocalDate();
                reservaCompleta.horaFuncion = rs.getTime("hora_funcion").toLocalTime();
                reservaCompleta.nombreSala = rs.getString("nombre_sala");
                reservaCompleta.capacidadSala = rs.getInt("Capacidad");
                reservaCompleta.asientos = rs.getString("asientos");
                reservaCompleta.cantidadAsientos = rs.getInt("cantidad_asientos");

                reservas.add(reservaCompleta);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas completas: " + e.getMessage());
            e.printStackTrace(); // Para ver el stack trace completo
        }
        return reservas;
    }

    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE Reserva SET Fecha_Reserva = ?, Estado = ?, Usuario_ID_Usuario = ?, Funcion_ID_Funcion = ? WHERE ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(reserva.getFechaReserva()));
            pstmt.setString(2, reserva.getEstado());
            pstmt.setInt(3, reserva.getUsuarioIdUsuario());
            pstmt.setInt(4, reserva.getFuncionIdFuncion());
            pstmt.setInt(5, reserva.getIdReserva());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar reserva: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int idReserva) {
        String sql = "DELETE FROM Reserva WHERE ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar reserva: " + e.getMessage());
        }
        return false;
    }

    // Método para insertar datos de prueba (puedes usar esto para probar)
    public void insertarDatosPrueba() {
        try (Connection conn = BDConnection.getConnection()) {
            // Insertar algunas reservas de prueba
            String sql = "INSERT INTO Reserva (Fecha_Reserva, Estado, Usuario_ID_Usuario, Funcion_ID_Funcion) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Reserva 1
            pstmt.setTimestamp(1, Timestamp.valueOf("2025-08-10 10:30:00"));
            pstmt.setString(2, "Confirmada");
            pstmt.setInt(3, 1); // Usuario con ID 1 (Arturo)
            pstmt.setInt(4, 2); // Función con ID 2
            pstmt.executeUpdate();

            // Reserva 2
            pstmt.setTimestamp(1, Timestamp.valueOf("2025-08-09 15:45:00"));
            pstmt.setString(2, "Pendiente");
            pstmt.setInt(3, 1); // Usuario con ID 1 (Arturo)
            pstmt.setInt(4, 5); // Función con ID 5
            pstmt.executeUpdate();

            // Reserva 3
            pstmt.setTimestamp(1, Timestamp.valueOf("2025-08-08 12:20:00"));
            pstmt.setString(2, "Cancelada");
            pstmt.setInt(3, 1); // Usuario con ID 1 (Arturo)
            pstmt.setInt(4, 8); // Función con ID 8
            pstmt.executeUpdate();

            System.out.println("Datos de prueba insertados correctamente");

        } catch (SQLException e) {
            System.err.println("Error al insertar datos de prueba: " + e.getMessage());
        }
    }

    public static class ReservaCompleta {
        public int idReserva;
        public LocalDateTime fechaReserva;
        public String estado;
        public String tituloPelicula;
        public String genero;
        public int duracion;
        public String clasificacion;
        public java.time.LocalDate fechaFuncion;
        public java.time.LocalTime horaFuncion;
        public String nombreSala;
        public int capacidadSala;
        public String asientos;
        public int cantidadAsientos;

        public String getEstadoFormateado() {
            switch (estado.toLowerCase()) {
                case "confirmada":
                    return "✓ Confirmada";
                case "pendiente":
                    return "⏳ Pendiente";
                case "cancelada":
                    return "❌ Cancelada";
                default:
                    return estado;
            }
        }

        public String getFechaReservaFormateada() {
            return fechaReserva.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getFechaFuncionFormateada() {
            return fechaFuncion.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }

        public String getHoraFuncionFormateada() {
            return horaFuncion.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
        }

        public String getDuracionFormateada() {
            int horas = duracion / 60;
            int minutos = duracion % 60;
            if (horas > 0) {
                return horas + "h " + minutos + "m";
            } else {
                return minutos + "m";
            }
        }
    }
}