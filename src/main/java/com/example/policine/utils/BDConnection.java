package com.example.policine.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BDConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/cinemax";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private BDConnection() {}

    /**
     * Obtiene una conexión a la base de datos
     * @return Connection objeto de conexión a MySQL
     * @throws SQLException si hay error en la conexión
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER);

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

    public static void main(String[] args) {
        testConnection();
    }
}