package com.example.policine.model.dao;

import com.example.policine.model.entities.Reserva;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    /**
     * Insertar una nueva reserva
     */
    public boolean insertar(Reserva reserva) {
        String sql = "INSERT INTO Reserva (Fecha_Reserva, Estado, ID_Usuario, ID_Funcion) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, Date.valueOf(reserva.getFechaReserva()));
            pstmt.setString(2, reserva.getEstado());
            pstmt.setInt(3, reserva.getIdUsuario());
            pstmt.setInt(4, reserva.getIdFuncion());

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

    /**
     * Buscar reserva por ID
     */
    public Reserva buscarPorId(int idReserva) {
        String sql = "SELECT * FROM Reserva WHERE ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("Fecha_Reserva").toLocalDate(),
                        rs.getString("Estado"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Funcion")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar reserva: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todas las reservas
     */
    public List<Reserva> listarTodos() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva ORDER BY Fecha_Reserva DESC";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("Fecha_Reserva").toLocalDate(),
                        rs.getString("Estado"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Funcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Listar reservas por usuario
     */
    public List<Reserva> listarPorUsuario(int idUsuario) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE ID_Usuario = ? ORDER BY Fecha_Reserva DESC";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("Fecha_Reserva").toLocalDate(),
                        rs.getString("Estado"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Funcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas por usuario: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Listar reservas por función
     */
    public List<Reserva> listarPorFuncion(int idFuncion) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE ID_Funcion = ? ORDER BY Fecha_Reserva";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("Fecha_Reserva").toLocalDate(),
                        rs.getString("Estado"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Funcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas por función: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Listar reservas por estado
     */
    public List<Reserva> listarPorEstado(String estado) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE Estado = ? ORDER BY Fecha_Reserva DESC";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, estado);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("Fecha_Reserva").toLocalDate(),
                        rs.getString("Estado"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Funcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas por estado: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Actualizar reserva
     */
    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE Reserva SET Fecha_Reserva = ?, Estado = ?, ID_Usuario = ?, ID_Funcion = ? WHERE ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(reserva.getFechaReserva()));
            pstmt.setString(2, reserva.getEstado());
            pstmt.setInt(3, reserva.getIdUsuario());
            pstmt.setInt(4, reserva.getIdFuncion());
            pstmt.setInt(5, reserva.getIdReserva());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar reserva: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cambiar estado de la reserva
     */
    public boolean cambiarEstado(int idReserva, String nuevoEstado) {
        String sql = "UPDATE Reserva SET Estado = ? WHERE ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idReserva);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado de reserva: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar reserva
     */
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

    /**
     * Contar reservas activas por función
     */
    public int contarReservasActivasPorFuncion(int idFuncion) {
        String sql = "SELECT COUNT(*) as total FROM Reserva WHERE ID_Funcion = ? AND Estado IN ('Confirmada', 'Pendiente')";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar reservas activas: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Buscar reservas por rango de fechas
     */
    public List<Reserva> buscarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE Fecha_Reserva BETWEEN ? AND ? ORDER BY Fecha_Reserva";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaInicio));
            pstmt.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getDate("Fecha_Reserva").toLocalDate(),
                        rs.getString("Estado"),
                        rs.getInt("ID_Usuario"),
                        rs.getInt("ID_Funcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar reservas por rango de fechas: " + e.getMessage());
        }
        return reservas;
    }
}