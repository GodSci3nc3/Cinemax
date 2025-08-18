package com.example.policine.model.dao;

import com.example.policine.model.entities.Reserva;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    /**
     * Insertar una nueva reserva
     */
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
                        rs.getTimestamp("Fecha_Reserva").toLocalDateTime(),
                        rs.getString("Estado"),
                        rs.getInt("Usuario_ID_Usuario"),
                        rs.getInt("Funcion_ID_Funcion")
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
    public List<Reserva> listarPorUsuario(int usuarioId) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE Usuario_ID_Usuario = ? ORDER BY Fecha_Reserva DESC";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getTimestamp("Fecha_Reserva").toLocalDateTime(),
                        rs.getString("Estado"),
                        rs.getInt("Usuario_ID_Usuario"),
                        rs.getInt("Funcion_ID_Funcion")
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
    public List<Reserva> listarPorFuncion(int funcionId) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM Reserva WHERE Funcion_ID_Funcion = ? ORDER BY Fecha_Reserva";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, funcionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(new Reserva(
                        rs.getInt("ID_Reserva"),
                        rs.getTimestamp("Fecha_Reserva").toLocalDateTime(),
                        rs.getString("Estado"),
                        rs.getInt("Usuario_ID_Usuario"),
                        rs.getInt("Funcion_ID_Funcion")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas por función: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Actualizar reserva
     */
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
    public int contarReservasActivasPorFuncion(int funcionId) {
        String sql = "SELECT COUNT(*) as total FROM Reserva WHERE Funcion_ID_Funcion = ? AND Estado IN ('Confirmada', 'Pendiente')";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, funcionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar reservas activas: " + e.getMessage());
        }
        return 0;
    }
}