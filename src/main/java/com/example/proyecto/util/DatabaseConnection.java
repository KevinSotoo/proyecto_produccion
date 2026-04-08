package com.example.proyecto.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        try {
            // Cargar propiedades del archivo
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input == null) {
                System.out.println("✗ Archivo database.properties no encontrado");
                // Valores por defecto para la universidad
                URL = "jdbc:mysql://172.30.16.76:3306/gym_db";
                USER = "kssoto29";
                PASSWORD = "67001429";
            } else {
                props.load(input);
                URL = props.getProperty("db.url");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");
                input.close();
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado correctamente");

        } catch (Exception e) {
            System.out.println("✗ Error al cargar configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Conexión exitosa a: " + URL);
            return conn;
        } catch (SQLException e) {
            System.out.println("✗ Error al conectar: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Prueba de conexión exitosa");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("✗ Error en la prueba de conexión: " + e.getMessage());
        }
    }

    public static String getConnectionInfo() {
        return "Conectado a: " + URL + " | Usuario: " + USER;
    }
}