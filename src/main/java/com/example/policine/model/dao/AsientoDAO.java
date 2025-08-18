package com.example.policine.model.dao;

import com.example.policine.model.entities.Asiento;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsientoDAO {

    public boolean insertar(Asiento asiento) {
        String sql = "INSERT INTO Asiento (Fila, Numero, Tipo, Sala_ID_Sala) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, asiento.getFila());
            pstmt.setInt(2, asiento.getNumero());
            pstmt.setString(3, asiento.getTipo());
            pstmt.setInt(4, asiento.getSalaIdSala());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    asiento.setIdAsiento(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar asiento: " + e.getMessage());
        }
        return false;
    }

    public Asiento buscarPorId(int idAsiento) {
        String sql = "SELECT * FROM Asiento WHERE ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsiento);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Asiento(
                        rs.getInt("ID_Asiento"),
                        rs.getInt("Fila"),
                        rs.getInt("Numero"),
                        rs.getString("Tipo"),
                        rs.getInt("Sala_ID_Sala")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar asiento: " + e.getMessage());
        }
        return null;
    }

    public Asiento buscarPorPosicion(int idSala, int fila, int numero) {
        String sql = "SELECT * FROM Asiento WHERE Sala_ID_Sala = ? AND Fila = ? AND Numero = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            pstmt.setInt(2, fila);
            pstmt.setInt(3, numero);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Asiento(
                        rs.getInt("ID_Asiento"),
                        rs.getInt("Fila"),
                        rs.getInt("Numero"),
                        rs.getString("Tipo"),
                        rs.getInt("Sala_ID_Sala")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar asiento por posición: " + e.getMessage());
        }
        return null;
    }

    public List<Asiento> listarPorSala(int idSala) {
        List<Asiento> asientos = new ArrayList<>();
        String sql = "SELECT * FROM Asiento WHERE Sala_ID_Sala = ? ORDER BY Fila, Numero";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                asientos.add(new Asiento(
                        rs.getInt("ID_Asiento"),
                        rs.getInt("Fila"),
                        rs.getInt("Numero"),
                        rs.getString("Tipo"),
                        rs.getInt("Sala_ID_Sala")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar asientos por sala: " + e.getMessage());
        }
        return asientos;
    }

    public List<Asiento> obtenerAsientosOcupados(int idFuncion) {
        List<Asiento> asientosOcupados = new ArrayList<>();
        String sql = "SELECT a.* FROM Asiento a " +
                "INNER JOIN Reserva_Asiento ra ON a.ID_Asiento = ra.Asiento_ID_Asiento " +
                "INNER JOIN Reserva r ON ra.Reserva_ID_Reserva = r.ID_Reserva " +
                "WHERE r.Funcion_ID_Funcion = ? AND r.Estado IN ('Confirmada', 'Pendiente') " +
                "ORDER BY a.Fila, a.Numero";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                asientosOcupados.add(new Asiento(
                        rs.getInt("ID_Asiento"),
                        rs.getInt("Fila"),
                        rs.getInt("Numero"),
                        rs.getString("Tipo"),
                        rs.getInt("Sala_ID_Sala")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asientos ocupados: " + e.getMessage());
        }
        return asientosOcupados;
    }

    public List<Asiento> obtenerAsientosDisponibles(int idFuncion, int idSala) {
        List<Asiento> asientosDisponibles = new ArrayList<>();
        String sql = "SELECT a.* FROM Asiento a " +
                "WHERE a.Sala_ID_Sala = ? " +
                "AND a.ID_Asiento NOT IN (" +
                "    SELECT ra.Asiento_ID_Asiento FROM Reserva_Asiento ra " +
                "    INNER JOIN Reserva r ON ra.Reserva_ID_Reserva = r.ID_Reserva " +
                "    WHERE r.Funcion_ID_Funcion = ? AND r.Estado IN ('Confirmada', 'Pendiente')" +
                ") " +
                "ORDER BY a.Fila, a.Numero";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            pstmt.setInt(2, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                asientosDisponibles.add(new Asiento(
                        rs.getInt("ID_Asiento"),
                        rs.getInt("Fila"),
                        rs.getInt("Numero"),
                        rs.getString("Tipo"),
                        rs.getInt("Sala_ID_Sala")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener asientos disponibles: " + e.getMessage());
        }
        return asientosDisponibles;
    }

    public boolean estaDisponible(int idAsiento, int idFuncion) {
        String sql = "SELECT COUNT(*) as total FROM Reserva_Asiento ra " +
                "INNER JOIN Reserva r ON ra.Reserva_ID_Reserva = r.ID_Reserva " +
                "WHERE ra.Asiento_ID_Asiento = ? AND r.Funcion_ID_Funcion = ? " +
                "AND r.Estado IN ('Confirmada', 'Pendiente')";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsiento);
            pstmt.setInt(2, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") == 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad del asiento: " + e.getMessage());
        }
        return false;
    }

    public int contarAsientosOcupados(int idFuncion) {
        String sql = "SELECT COUNT(*) as total FROM Reserva_Asiento ra " +
                "INNER JOIN Reserva r ON ra.Reserva_ID_Reserva = r.ID_Reserva " +
                "WHERE r.Funcion_ID_Funcion = ? AND r.Estado IN ('Confirmada', 'Pendiente')";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar asientos ocupados: " + e.getMessage());
        }
        return 0;
    }

    public boolean generarAsientosPorSala(int idSala, int numeroFilas, int asientosPorFila, String tipoDefault) {
        String sql = "INSERT INTO Asiento (Fila, Numero, Tipo, Sala_ID_Sala) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int fila = 1; fila <= numeroFilas; fila++) {
                for (int numero = 1; numero <= asientosPorFila; numero++) {
                    pstmt.setInt(1, fila);
                    pstmt.setInt(2, numero);
                    pstmt.setString(3, tipoDefault);
                    pstmt.setInt(4, idSala);
                    pstmt.addBatch();
                }
            }

            int[] resultados = pstmt.executeBatch();
            return resultados.length > 0;

        } catch (SQLException e) {
            System.err.println("Error al generar asientos: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizar(Asiento asiento) {
        String sql = "UPDATE Asiento SET Fila = ?, Numero = ?, Tipo = ?, Sala_ID_Sala = ? WHERE ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, asiento.getFila());
            pstmt.setInt(2, asiento.getNumero());
            pstmt.setString(3, asiento.getTipo());
            pstmt.setInt(4, asiento.getSalaIdSala());
            pstmt.setInt(5, asiento.getIdAsiento());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar asiento: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminar(int idAsiento) {
        String sql = "DELETE FROM Asiento WHERE ID_Asiento = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAsiento);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar asiento: " + e.getMessage());
        }
        return false;
    }

    // 🔹 Nuevo método tolerante para buscar por código textual
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

            if (fila1Based - 1 >= 0) {
                a = buscarPorPosicion(idSala, fila1Based - 1, numero);
                if (a != null) return a;
            }
            return null;

        } catch (NumberFormatException e) {
            return null;
        }
    }
}
