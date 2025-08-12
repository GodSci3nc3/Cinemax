package com.example.policine.model.dao;

import com.example.policine.model.entities.Cine;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CineDAO {

    /**
     * Insertar un nuevo cine
     */
    public boolean insertar(Cine cine) {
        String sql = "INSERT INTO Cine (Nombre, Direccion, Ciudad) VALUES (?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, cine.getNombre());
            pstmt.setString(2, cine.getDireccion());
            pstmt.setString(3, cine.getCiudad());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cine.setIdCine(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar cine: " + e.getMessage());
        }
        return false;
    }

    /**
     * Buscar cine por ID
     */
    public Cine buscarPorId(int idCine) {
        String sql = "SELECT * FROM Cine WHERE ID_Cine = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCine);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Cine(
                        rs.getInt("ID_Cine"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Ciudad")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cine: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todos los cines
     */
    public List<Cine> listarTodos() {
        List<Cine> cines = new ArrayList<>();
        String sql = "SELECT * FROM Cine";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cines.add(new Cine(
                        rs.getInt("ID_Cine"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Ciudad")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar cines: " + e.getMessage());
        }
        return cines;
    }

    /**
     * Buscar cines por ciudad
     */
    public List<Cine> buscarPorCiudad(String ciudad) {
        List<Cine> cines = new ArrayList<>();
        String sql = "SELECT * FROM Cine WHERE Ciudad = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, ciudad);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                cines.add(new Cine(
                        rs.getInt("ID_Cine"),
                        rs.getString("Nombre"),
                        rs.getString("Direccion"),
                        rs.getString("Ciudad")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar cines por ciudad: " + e.getMessage());
        }
        return cines;
    }

    /**
     * Actualizar cine
     */
    public boolean actualizar(Cine cine) {
        String sql = "UPDATE Cine SET Nombre = ?, Direccion = ?, Ciudad = ? WHERE ID_Cine = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cine.getNombre());
            pstmt.setString(2, cine.getDireccion());
            pstmt.setString(3, cine.getCiudad());
            pstmt.setInt(4, cine.getIdCine());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar cine: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar cine
     */
    public boolean eliminar(int idCine) {
        String sql = "DELETE FROM Cine WHERE ID_Cine = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idCine);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar cine: " + e.getMessage());
        }
        return false;
    }
}