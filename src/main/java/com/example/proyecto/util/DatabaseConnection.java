package com.example.proyecto.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public enum DatabaseEngine {
        MYSQL,
        SQLITE
    }

    private static String url;
    private static String user;
    private static String password;
    private static final Properties props = new Properties();
    private static DatabaseEngine currentEngine = DatabaseEngine.MYSQL;

    static {
        try {
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input == null) {
                System.out.println("Archivo database.properties no encontrado, usando valores por defecto");
            } else {
                props.load(input);
                input.close();
            }

            applyEngineConfig(currentEngine);

        } catch (Exception e) {
            System.out.println("Error al cargar configuracion de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void applyEngineConfig(DatabaseEngine engine) throws ClassNotFoundException {
        if (engine == DatabaseEngine.SQLITE) {
            url = getPropertyOrDefault("sqlite.url", "jdbc:sqlite:data/gym_db.sqlite");
            user = getPropertyOrDefault("sqlite.user", "");
            password = getPropertyOrDefault("sqlite.password", "");
            Class.forName("org.sqlite.JDBC");
            System.out.println("Motor de BD seleccionado: SQLITE");
            return;
        }

        // Compatibilidad con configuracion antigua db.*
        url = getPropertyOrDefault("mysql.url", getPropertyOrDefault("db.url", "jdbc:mysql://localhost:3306/gym_db"));
        user = getPropertyOrDefault("mysql.user", getPropertyOrDefault("db.user", "root"));
        password = getPropertyOrDefault("mysql.password", getPropertyOrDefault("db.password", ""));
        Class.forName("com.mysql.cj.jdbc.Driver");
        System.out.println("Motor de BD seleccionado: MYSQL");
    }

    private static String getPropertyOrDefault(String key, String defaultValue) {
        String value = props.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static synchronized void setEngine(DatabaseEngine engine) {
        try {
            currentEngine = engine;
            applyEngineConfig(engine);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar el driver para " + engine, e);
        }
    }

    public static DatabaseEngine getEngine() {
        return currentEngine;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn;
            if (currentEngine == DatabaseEngine.SQLITE) {
                conn = DriverManager.getConnection(url);
            } else {
                conn = DriverManager.getConnection(url, user, password);
            }
            System.out.println("Conexion exitosa a: " + url);
            return conn;
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Prueba de conexion exitosa");
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error en la prueba de conexion: " + e.getMessage());
        }
    }

    public static String getConnectionInfo() {
        if (currentEngine == DatabaseEngine.SQLITE) {
            return "Motor: SQLITE | URL: " + url;
        }
        return "Motor: MYSQL | URL: " + url + " | Usuario: " + user;
    }
}