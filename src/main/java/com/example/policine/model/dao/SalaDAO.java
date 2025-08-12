package com.example.policine.model.dao;

import com.example.policine.model.entities.Sala;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    /**
     * Insertar una nueva sala
     */
    public boolean insertar(Sala sala) {
        String sql = "INSERT INTO Sala (Nombre_Sala, Capacidad, Tipo, Cine_ID_Cine) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, sala.getNombreSala());
            pstmt.setInt(2, sala.getCapacidad());
            pstmt.setString(3, sala.getTipo());
            pstmt.setInt(4, sala.getIdCine());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    sala.setIdSala(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar sala: " + e.getMessage());
        }
        return false;
    }

    /**
     * Buscar sala por ID
     */
    public Sala buscarPorId(int idSala) {
        String sql = "SELECT * FROM Sala WHERE ID_Sala = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Sala(
                        rs.getInt("ID_Sala"),
                        rs.getString("Nombre_Sala"),
                        rs.getInt("Capacidad"),
                        rs.getString("Tipo"),
                        rs.getInt("Cine_ID_Cine")  // CAMBIADO AQUÍ
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar sala: " + e.getMessage());
        }
        return null;
    }

    /**
     * Buscar sala por nombre
     */
    public Sala buscarPorNombre(String nombreSala) {
        String sql = "SELECT * FROM Sala WHERE Nombre_Sala = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreSala);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Sala(
                        rs.getInt("ID_Sala"),
                        rs.getString("Nombre_Sala"),
                        rs.getInt("Capacidad"),
                        rs.getString("Tipo"),
                        rs.getInt("Cine_ID_Cine")  // CAMBIADO AQUÍ
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar sala por nombre: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todas las salas
     */
    public List<Sala> listarTodos() {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM Sala ORDER BY Nombre_Sala";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                salas.add(new Sala(
                        rs.getInt("ID_Sala"),
                        rs.getString("Nombre_Sala"),
                        rs.getInt("Capacidad"),
                        rs.getString("Tipo"),
                        rs.getInt("Cine_ID_Cine")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar salas: " + e.getMessage());
        }
        return salas;
    }

    /**
     * Listar salas por tipo
     */
    public List<Sala> listarPorTipo(String tipo) {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM Sala WHERE Tipo = ? ORDER BY Nombre_Sala";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                salas.add(new Sala(
                        rs.getInt("ID_Sala"),
                        rs.getString("Nombre_Sala"),
                        rs.getInt("Capacidad"),
                        rs.getString("Tipo"),
                        rs.getInt("Cine_ID_Cine")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar salas por tipo: " + e.getMessage());
        }
        return salas;
    }

    /**
     * Actualizar sala
     */
    public boolean actualizar(Sala sala) {
        String sql = "UPDATE Sala SET Nombre_Sala = ?, Capacidad = ?, Tipo = ?, Cine_ID_Cine = ? WHERE ID_Sala = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sala.getNombreSala());
            pstmt.setInt(2, sala.getCapacidad());
            pstmt.setString(3, sala.getTipo());
            pstmt.setInt(4, sala.getIdCine());
            pstmt.setInt(5, sala.getIdSala());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar sala: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar sala
     */
    public boolean eliminar(int idSala) {
        String sql = "DELETE FROM Sala WHERE ID_Sala = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar sala: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verificar si una sala existe por nombre
     */
    public boolean existePorNombre(String nombreSala) {
        String sql = "SELECT COUNT(*) as total FROM Sala WHERE Nombre_Sala = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombreSala);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de sala: " + e.getMessage());
        }
        return false;
    }

    /**
     * Listar salas por cine
     */
    public List<Sala> listarPorCine(int idCine) {
        List<Sala> salas = new ArrayList<>();
        String sql = "SELECT * FROM Sala WHERE Cine_ID_Cine = ? ORDER BY Nombre_Sala";  // CAMBIADO AQUÍ

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCine);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                salas.add(new Sala(
                        rs.getInt("ID_Sala"),
                        rs.getString("Nombre_Sala"),
                        rs.getInt("Capacidad"),
                        rs.getString("Tipo"),
                        rs.getInt("Cine_ID_Cine")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar salas por cine: " + e.getMessage());
        }
        return salas;
    }
}