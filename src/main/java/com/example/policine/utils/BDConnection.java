package com.example.policine.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConnection {

    // Configuración de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/policine_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Cambiar por tu contraseña
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    // Constructor privado para evitar instanciación
    private BDConnection() {}

    /**
     * Obtiene una conexión a la base de datos
     * @return Connection objeto de conexión a MySQL
     * @throws SQLException si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de MySQL
            Class.forName(DRIVER);

            // Crear y retornar la conexión
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: No se pudo cargar el driver de MySQL", e);
        } catch (SQLException e) {
            throw new SQLException("Error: No se pudo conectar a la base de datos", e);
        }
    }

    /**
     * Cierra una conexión de forma segura
     * @param connection la conexión a cerrar
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexión cerrada exitosamente");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    /**
     * Método para probar la conexión
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            System.out.println("✅ Conexión a la base de datos exitosa!");
            System.out.println("Database: " + connection.getCatalog());
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Error en la conexión: " + e.getMessage());
            return false;
        }
    }

    // Método main para probar la conexión
    public static void main(String[] args) {
        testConnection();
    }
}