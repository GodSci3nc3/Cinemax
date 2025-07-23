package com.example.policine.model.dao;

import com.example.policine.model.entities.Usuario;
import com.example.policine.utils.BDConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /**
     * Insertar un nuevo usuario en la base de datos
     */
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO Usuario (Nombre, Apellido, Correo_Electronico, Contrasena, Telefono) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getCorreoElectronico());
            pstmt.setString(4, usuario.getContrasena());
            pstmt.setString(5, usuario.getTelefono());

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    usuario.setIdUsuario(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Buscar usuario por ID
     */
    public Usuario buscarPorId(int idUsuario) {
        String sql = "SELECT * FROM Usuario WHERE ID_Usuario = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("ID_Usuario"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Correo_Electronico"),
                        rs.getString("Contrasena"),
                        rs.getString("Telefono")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Buscar usuario por email
     */
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM Usuario WHERE Correo_Electronico = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("ID_Usuario"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Correo_Electronico"),
                        rs.getString("Contrasena"),
                        rs.getString("Telefono")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
        }
        return null;
    }

    /**
     * Listar todos los usuarios
     */
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM Usuario";

        try (Connection conn = BDConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("ID_Usuario"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Correo_Electronico"),
                        rs.getString("Contrasena"),
                        rs.getString("Telefono")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Actualizar usuario
     */
    public boolean actualizar(Usuario usuario) {
        String sql = "UPDATE Usuario SET Nombre = ?, Apellido = ?, Correo_Electronico = ?, Contrasena = ?, Telefono = ? WHERE ID_Usuario = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getCorreoElectronico());
            pstmt.setString(4, usuario.getContrasena());
            pstmt.setString(5, usuario.getTelefono());
            pstmt.setInt(6, usuario.getIdUsuario());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Eliminar usuario
     */
    public boolean eliminar(int idUsuario) {
        String sql = "DELETE FROM Usuario WHERE ID_Usuario = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        return false;
    }

    /**
     * Autenticar usuario (login)
     */
    public Usuario autenticar(String email, String contrasena) {
        String sql = "SELECT * FROM Usuario WHERE Correo_Electronico = ? AND Contrasena = ?";

        try (Connection conn = BDConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Usuario(
                        rs.getInt("ID_Usuario"),
                        rs.getString("Nombre"),
                        rs.getString("Apellido"),
                        rs.getString("Correo_Electronico"),
                        rs.getString("Contrasena"),
                        rs.getString("Telefono")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }
}