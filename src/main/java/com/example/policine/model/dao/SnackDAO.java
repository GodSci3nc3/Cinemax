package com.example.policine.model.dao;

import com.example.policine.model.entities.Snack;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SnackDAO {

    /**
     * Insertar un nuevo snack
     */
    public boolean insertar(Snack snack) {
        String sql = "INSERT INTO Snack (Nombre, Precio, Descripcion, Tipo) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, snack.getNombre());
            pstmt.setDouble(2, snack.getPrecio());
            pstmt.setString(3, snack.getDescripcion());
            pstmt.setString(4, snack.getTipo());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    snack.setIdSnack(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar snack: " + e.getMessage());
        }
        return false;
    }

    /**
     * Buscar snack por ID
     */
    public Snack buscarPorId(int idSnack) {
        String sql = "SELECT * FROM Snack WHERE ID_Snack = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSnack);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Snack(
                        rs.getInt("ID_Snack"),
                        rs.getString("Nombre"),
                        rs.getDouble("Precio"),
                        rs.getString("Descripcion"),
                        rs.getString("Tipo")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar snack: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todos los snacks
     */
    public List<Snack> listarTodos() {
        List<Snack> snacks = new ArrayList<>();
        String sql = "SELECT * FROM Snack ORDER BY Tipo, Nombre";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                snacks.add(new Snack(
                        rs.getInt("ID_Snack"),
                        rs.getString("Nombre"),
                        rs.getDouble("Precio"),
                        rs.getString("Descripcion"),
                        rs.getString("Tipo")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar snacks: " + e.getMessage());
        }
        return snacks;
    }

    /**
     * Buscar snacks por tipo
     */
    public List<Snack> buscarPorTipo(String tipo) {
        List<Snack> snacks = new ArrayList<>();
        String sql = "SELECT * FROM Snack WHERE Tipo = ? ORDER BY Nombre";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                snacks.add(new Snack(
                        rs.getInt("ID_Snack"),
                        rs.getString("Nombre"),
                        rs.getDouble("Precio"),
                        rs.getString("Descripcion"),
                        rs.getString("Tipo")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar snacks por tipo: " + e.getMessage());
        }
        return snacks;
    }

    /**
     * Buscar snacks por nombre (búsqueda parcial)
     */
    public List<Snack> buscarPorNombre(String nombre) {
        List<Snack> snacks = new ArrayList<>();
        String sql = "SELECT * FROM Snack WHERE Nombre LIKE ? ORDER BY Nombre";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                snacks.add(new Snack(
                        rs.getInt("ID_Snack"),
                        rs.getString("Nombre"),
                        rs.getDouble("Precio"),
                        rs.getString("Descripcion"),
                        rs.getString("Tipo")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar snacks por nombre: " + e.getMessage());
        }
        return snacks;
    }

    /**
     * Buscar snacks por rango de precio
     */
    public List<Snack> buscarPorRangoPrecio(double precioMin, double precioMax) {
        List<Snack> snacks = new ArrayList<>();
        String sql = "SELECT * FROM Snack WHERE Precio BETWEEN ? AND ? ORDER BY Precio";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, precioMin);
            pstmt.setDouble(2, precioMax);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                snacks.add(new Snack(
                        rs.getInt("ID_Snack"),
                        rs.getString("Nombre"),
                        rs.getDouble("Precio"),
                        rs.getString("Descripcion"),
                        rs.getString("Tipo")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar snacks por rango de precio: " + e.getMessage());
        }
        return snacks;
    }

    /**
     * Actualizar snack
     */
    public boolean actualizar(Snack snack) {
        String sql = "UPDATE Snack SET Nombre = ?, Precio = ?, Descripcion = ?, Tipo = ? WHERE ID_Snack = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, snack.getNombre());
            pstmt.setDouble(2, snack.getPrecio());
            pstmt.setString(3, snack.getDescripcion());
            pstmt.setString(4, snack.getTipo());
            pstmt.setInt(5, snack.getIdSnack());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar snack: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar snack
     */
    public boolean eliminar(int idSnack) {
        String sql = "DELETE FROM Snack WHERE ID_Snack = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSnack);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar snack: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtener tipos de snacks disponibles
     */
    public List<String> obtenerTiposDisponibles() {
        List<String> tipos = new ArrayList<>();
        String sql = "SELECT DISTINCT Tipo FROM Snack ORDER BY Tipo";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tipos.add(rs.getString("Tipo"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de snacks: " + e.getMessage());
        }
        return tipos;
    }

    /**
     * Contar snacks por tipo
     */
    public int contarPorTipo(String tipo) {
        String sql = "SELECT COUNT(*) as total FROM Snack WHERE Tipo = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tipo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar snacks por tipo: " + e.getMessage());
        }
        return 0;
    }
}