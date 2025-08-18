package com.example.policine.model.dao;

import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaAsientoDAO {

    /**
     * Insertar una nueva relación reserva-asiento
     */
    public boolean insertar(int idReserva, int idAsiento) {
        String sql = "INSERT INTO Reserva_Asiento (Reserva_ID_Reserva, Asiento_ID_Asiento) VALUES (?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            pstmt.setInt(2, idAsiento);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar reserva-asiento: " + e.getMessage());
        }
        return false;
    }

    /**
     * Insertar múltiples asientos para una reserva
     */
    public boolean insertarMultiples(int idReserva, List<Integer> idsAsientos) {
        if (idsAsientos == null || idsAsientos.isEmpty()) {
            return true; // No hay asientos que insertar
        }

        String sql = "INSERT INTO Reserva_Asiento (Reserva_ID_Reserva, Asiento_ID_Asiento) VALUES (?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int idAsiento : idsAsientos) {
                pstmt.setInt(1, idReserva);
                pstmt.setInt(2, idAsiento);
                pstmt.addBatch();
            }

            int[] resultados = pstmt.executeBatch();

            // Verificar que todos se insertaron correctamente
            for (int resultado : resultados) {
                if (resultado <= 0) {
                    return false;
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar múltiples reserva-asientos: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtener IDs de asientos por reserva
     */
    public List<Integer> obtenerAsientosPorReserva(int idReserva) {
        List<Integer> asientos = new ArrayList<>();
        String sql = "SELECT Asiento_ID_Asiento FROM Reserva_Asiento WHERE Reserva_ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                asientos.add(rs.getInt("Asiento_ID_Asiento"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asientos por reserva: " + e.getMessage());
        }
        return asientos;
    }

    /**
     * Obtener IDs de reservas por asiento
     */
    public List<Integer> obtenerReservasPorAsiento(int idAsiento) {
        List<Integer> reservas = new ArrayList<>();
        String sql = "SELECT Reserva_ID_Reserva FROM Reserva_Asiento WHERE Asiento_ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsiento);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservas.add(rs.getInt("Reserva_ID_Reserva"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener reservas por asiento: " + e.getMessage());
        }
        return reservas;
    }

    /**
     * Verificar si existe la relación reserva-asiento
     */
    public boolean existe(int idReserva, int idAsiento) {
        String sql = "SELECT COUNT(*) as total FROM Reserva_Asiento " +
                "WHERE Reserva_ID_Reserva = ? AND Asiento_ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            pstmt.setInt(2, idAsiento);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar existencia reserva-asiento: " + e.getMessage());
        }
        return false;
    }

    /**
     * Contar asientos por reserva
     */
    public int contarAsientosPorReserva(int idReserva) {
        String sql = "SELECT COUNT(*) as total FROM Reserva_Asiento WHERE Reserva_ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar asientos por reserva: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Eliminar relación reserva-asiento específica
     */
    public boolean eliminar(int idReserva, int idAsiento) {
        String sql = "DELETE FROM Reserva_Asiento WHERE Reserva_ID_Reserva = ? AND Asiento_ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            pstmt.setInt(2, idAsiento);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar reserva-asiento: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar todos los asientos de una reserva
     */
    public boolean eliminarPorReserva(int idReserva) {
        String sql = "DELETE FROM Reserva_Asiento WHERE Reserva_ID_Reserva = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReserva);
            return pstmt.executeUpdate() >= 0; // >= 0 porque puede ser 0 si no había asientos

        } catch (SQLException e) {
            System.err.println("Error al eliminar asientos por reserva: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar todas las reservas de un asiento
     */
    public boolean eliminarPorAsiento(int idAsiento) {
        String sql = "DELETE FROM Reserva_Asiento WHERE Asiento_ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsiento);
            return pstmt.executeUpdate() >= 0; // >= 0 porque puede ser 0 si no había reservas

        } catch (SQLException e) {
            System.err.println("Error al eliminar reservas por asiento: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtener información completa de asientos reservados para una función
     */
    public List<String> obtenerAsientosReservadosParaFuncion(int idFuncion) {
        List<String> asientosReservados = new ArrayList<>();
        String sql = "SELECT a.Fila, a.Numero FROM Asiento a " +
                "INNER JOIN Reserva_Asiento ra ON a.ID_Asiento = ra.Asiento_ID_Asiento " +
                "INNER JOIN Reserva r ON ra.Reserva_ID_Reserva = r.ID_Reserva " +
                "WHERE r.Funcion_ID_Funcion = ? AND r.Estado IN ('Confirmada', 'Pendiente') " +
                "ORDER BY a.Fila, a.Numero";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int fila = rs.getInt("Fila");
                int numero = rs.getInt("Numero");
                char letraFila = (char) ('A' + (fila - 1));
                asientosReservados.add(letraFila + String.valueOf(numero));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asientos reservados para función: " + e.getMessage());
        }
        return asientosReservados;
    }
}