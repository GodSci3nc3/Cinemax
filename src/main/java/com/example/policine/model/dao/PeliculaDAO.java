package com.example.policine.model.dao;

import com.example.policine.model.entities.Pelicula;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaDAO {

    /**
     * Insertar una nueva película
     */
    public boolean insertar(Pelicula pelicula) {
        String sql = "INSERT INTO Pelicula (Titulo, Genero, Clasificacion, Duracion, Idioma, Sinopsis) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pelicula.getTitulo());
            pstmt.setString(2, pelicula.getGenero());
            pstmt.setString(3, pelicula.getClasificacion());
            pstmt.setInt(4, pelicula.getDuracion());
            pstmt.setString(5, pelicula.getIdioma());
            pstmt.setString(6, pelicula.getSinopsis());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    pelicula.setIdPelicula(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar película: " + e.getMessage());
        }
        return false;
    }

    /**
     * Buscar película por ID
     */
    public Pelicula buscarPorId(int idPelicula) {
        String sql = "SELECT * FROM Pelicula WHERE ID_Pelicula = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPelicula);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("Clasificacion"),
                        rs.getInt("Duracion"),
                        rs.getString("Idioma"),
                        rs.getString("Sinopsis")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar película: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todas las películas
     */
    public List<Pelicula> listarTodos() {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM Pelicula";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                peliculas.add(new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("Clasificacion"),
                        rs.getInt("Duracion"),
                        rs.getString("Idioma"),
                        rs.getString("Sinopsis")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar películas: " + e.getMessage());
        }
        return peliculas;
    }

    /**
     * Buscar películas por género
     */
    public List<Pelicula> buscarPorGenero(String genero) {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM Pelicula WHERE Genero = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, genero);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                peliculas.add(new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("Clasificacion"),
                        rs.getInt("Duracion"),
                        rs.getString("Idioma"),
                        rs.getString("Sinopsis")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar películas por género: " + e.getMessage());
        }
        return peliculas;
    }

    /**
     * Buscar películas por título (búsqueda parcial)
     */
    public List<Pelicula> buscarPorTitulo(String titulo) {
        List<Pelicula> peliculas = new ArrayList<>();
        String sql = "SELECT * FROM Pelicula WHERE Titulo LIKE ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + titulo + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                peliculas.add(new Pelicula(
                        rs.getInt("ID_Pelicula"),
                        rs.getString("Titulo"),
                        rs.getString("Genero"),
                        rs.getString("Clasificacion"),
                        rs.getInt("Duracion"),
                        rs.getString("Idioma"),
                        rs.getString("Sinopsis")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar películas por título: " + e.getMessage());
        }
        return peliculas;
    }

    /**
     * Actualizar película
     */
    public boolean actualizar(Pelicula pelicula) {
        String sql = "UPDATE Pelicula SET Titulo = ?, Genero = ?, Clasificacion = ?, Duracion = ?, Idioma = ?, Sinopsis = ? WHERE ID_Pelicula = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pelicula.getTitulo());
            pstmt.setString(2, pelicula.getGenero());
            pstmt.setString(3, pelicula.getClasificacion());
            pstmt.setInt(4, pelicula.getDuracion());
            pstmt.setString(5, pelicula.getIdioma());
            pstmt.setString(6, pelicula.getSinopsis());
            pstmt.setInt(7, pelicula.getIdPelicula());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar película: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar película
     */
    public boolean eliminar(int idPelicula) {
        String sql = "DELETE FROM Pelicula WHERE ID_Pelicula = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPelicula);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar película: " + e.getMessage());
        }
        return false;
    }
}