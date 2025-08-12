package com.example.policine.model.dao;

import com.example.policine.model.entities.Funcion;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionDAO {

    /**
     * Insertar una nueva función
     */
    public boolean insertar(Funcion funcion) {
        String sql = "INSERT INTO Funcion (Fecha, Hora, Pelicula_ID_Pelicula, Sala_ID_Sala) VALUES (?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDate(1, Date.valueOf(funcion.getFecha()));
            pstmt.setTime(2, Time.valueOf(funcion.getHora()));
            pstmt.setInt(3, funcion.getIdPelicula());
            pstmt.setInt(4, funcion.getIdSala());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    funcion.setIdFuncion(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar función: " + e.getMessage());
        }
        return false;
    }

    /**
     * Buscar función por ID
     */
    public Funcion buscarPorId(int idFuncion) {
        String sql = "SELECT * FROM Funcion WHERE ID_Funcion = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Funcion(
                        rs.getInt("ID_Funcion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getTime("Hora").toLocalTime(),
                        rs.getInt("Pelicula_ID_Pelicula"),  // CAMBIADO AQUÍ
                        rs.getInt("Sala_ID_Sala")  // CAMBIADO AQUÍ
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar función: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todas las funciones
     */
    public List<Funcion> listarTodos() {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM Funcion ORDER BY Fecha, Hora";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                funciones.add(new Funcion(
                        rs.getInt("ID_Funcion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getTime("Hora").toLocalTime(),
                        rs.getInt("Pelicula_ID_Pelicula"),  // CAMBIADO AQUÍ
                        rs.getInt("Sala_ID_Sala")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar funciones: " + e.getMessage());
        }
        return funciones;
    }

    /**
     * Listar funciones por película
     */
    public List<Funcion> listarPorPelicula(int idPelicula) {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM Funcion WHERE Pelicula_ID_Pelicula = ? ORDER BY Fecha, Hora";  // CAMBIADO AQUÍ

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                funciones.add(new Funcion(
                        rs.getInt("ID_Funcion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getTime("Hora").toLocalTime(),
                        rs.getInt("Pelicula_ID_Pelicula"),  // CAMBIADO AQUÍ
                        rs.getInt("Sala_ID_Sala")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar funciones por película: " + e.getMessage());
        }
        return funciones;
    }

    /**
     * Listar funciones por sala
     */
    public List<Funcion> listarPorSala(int idSala) {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM Funcion WHERE Sala_ID_Sala = ? ORDER BY Fecha, Hora";  // CAMBIADO AQUÍ

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                funciones.add(new Funcion(
                        rs.getInt("ID_Funcion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getTime("Hora").toLocalTime(),
                        rs.getInt("Pelicula_ID_Pelicula"),  // CAMBIADO AQUÍ
                        rs.getInt("Sala_ID_Sala")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar funciones por sala: " + e.getMessage());
        }
        return funciones;
    }

    /**
     * Listar funciones por fecha
     */
    public List<Funcion> listarPorFecha(LocalDate fecha) {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM Funcion WHERE Fecha = ? ORDER BY Hora";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fecha));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                funciones.add(new Funcion(
                        rs.getInt("ID_Funcion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getTime("Hora").toLocalTime(),
                        rs.getInt("Pelicula_ID_Pelicula"),  // CAMBIADO AQUÍ
                        rs.getInt("Sala_ID_Sala")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar funciones por fecha: " + e.getMessage());
        }
        return funciones;
    }

    /**
     * Buscar funciones por película y fecha
     */
    public List<Funcion> buscarPorPeliculaYFecha(int idPelicula, LocalDate fecha) {
        List<Funcion> funciones = new ArrayList<>();
        String sql = "SELECT * FROM Funcion WHERE Pelicula_ID_Pelicula = ? AND Fecha = ? ORDER BY Hora";  // CAMBIADO AQUÍ

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPelicula);
            pstmt.setDate(2, Date.valueOf(fecha));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                funciones.add(new Funcion(
                        rs.getInt("ID_Funcion"),
                        rs.getDate("Fecha").toLocalDate(),
                        rs.getTime("Hora").toLocalTime(),
                        rs.getInt("Pelicula_ID_Pelicula"),  // CAMBIADO AQUÍ
                        rs.getInt("Sala_ID_Sala")  // CAMBIADO AQUÍ
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar funciones por película y fecha: " + e.getMessage());
        }
        return funciones;
    }

    /**
     * Actualizar función
     */
    public boolean actualizar(Funcion funcion) {
        String sql = "UPDATE Funcion SET Fecha = ?, Hora = ?, Pelicula_ID_Pelicula = ?, Sala_ID_Sala = ? WHERE ID_Funcion = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(funcion.getFecha()));
            pstmt.setTime(2, Time.valueOf(funcion.getHora()));
            pstmt.setInt(3, funcion.getIdPelicula());
            pstmt.setInt(4, funcion.getIdSala());
            pstmt.setInt(5, funcion.getIdFuncion());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar función: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar función
     */
    public boolean eliminar(int idFuncion) {
        String sql = "DELETE FROM Funcion WHERE ID_Funcion = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idFuncion);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar función: " + e.getMessage());
        }
        return false;
    }

    /**
     * Verificar disponibilidad de sala para una función
     */
    public boolean verificarDisponibilidadSala(int idSala, LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) as total FROM Funcion WHERE Sala_ID_Sala = ? AND Fecha = ? AND Hora = ?";  // CAMBIADO AQUÍ

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idSala);
            pstmt.setDate(2, Date.valueOf(fecha));
            pstmt.setTime(3, Time.valueOf(hora));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") == 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar disponibilidad: " + e.getMessage());
        }
        return false;
    }
}