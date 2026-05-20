package com.example.proyecto.service;

import com.example.proyecto.util.DatabaseConnection;
import org.bson.Document;

// ...existing code...
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// ...existing code...

public class CuentaService {

    // Eliminado manejo de JSON local. Ahora las cuentas se obtienen/guardan únicamente desde la BD (MySQL/SQLite) o MongoDB.

    public CuentaUsuario buscarCuenta(String username, String password) {
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            Document doc = MongoDBService.obtenerCuentaPorUsername(username);
            if (doc != null && password.equals(doc.getString("password"))) {
                CuentaUsuario cuenta = new CuentaUsuario();
                cuenta.setUsername(doc.getString("username"));
                cuenta.setPassword(doc.getString("password"));
                cuenta.setRol(doc.getString("rol"));
                return cuenta;
            }
            return null;
        } else {
            // Buscar en BD relacional (MySQL/SQLite)
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT username, password, rol FROM cuentas WHERE username = ? AND password = ? LIMIT 1";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            CuentaUsuario cuenta = new CuentaUsuario();
                            cuenta.setUsername(rs.getString("username"));
                            cuenta.setPassword(rs.getString("password"));
                            cuenta.setRol(rs.getString("rol"));
                            return cuenta;
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("✗ Error al buscar cuenta en BD: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Busca una cuenta por documento (username será igual al documento)
     */
    public CuentaUsuario buscarCuentaPorDocumento(String documento) {
        // Buscar por documento únicamente en la BD relacional (por compatibilidad). Si se usa MongoDB, usar obtenerCuentaPorUsername
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            Document doc = MongoDBService.obtenerCuentaPorUsername(documento);
            if (doc == null) return null;
            CuentaUsuario cuenta = new CuentaUsuario();
            cuenta.setUsername(doc.getString("username"));
            cuenta.setPassword(doc.getString("password"));
            cuenta.setRol(doc.getString("rol"));
            return cuenta;
        }
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT username, password, rol FROM cuentas WHERE username = ? LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, documento);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        CuentaUsuario cuenta = new CuentaUsuario();
                        cuenta.setUsername(rs.getString("username"));
                        cuenta.setPassword(rs.getString("password"));
                        cuenta.setRol(rs.getString("rol"));
                        return cuenta;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al buscar cuenta por documento en BD: " + e.getMessage());
        }
        return null;
    }

    public void registrarCuenta(CuentaUsuario cuenta) {
        // Registrar la cuenta en la BD o Mongo según el engine actual
        if (cuenta == null) return;
        guardarCuenta(cuenta.getUsername(), cuenta.getPassword(), cuenta.getRol());
    }

    /**
     * Guarda una cuenta en la base de datos
     * Si es admin, insertamos sin usuario_id
     * Si el usuario es regular, intenta buscar por documento (username)
     */
    public void guardarCuenta(String username, String password, String rol) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Si es admin, insertamos sin usuario_id
            if ("admin".equalsIgnoreCase(rol)) {
                String sql = "INSERT INTO cuentas (username, password, rol) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, rol);
                    stmt.executeUpdate();
                    System.out.println("✓ Cuenta " + username + " guardada correctamente");
                }
            } else {
                // Para usuarios regulares, buscar por documento si el username es un documento
                String sql = "INSERT INTO cuentas (username, password, rol, usuario_id) VALUES (?, ?, ?, (SELECT id FROM usuarios WHERE documento = ? LIMIT 1))";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, rol);
                    stmt.setString(4, username); // Usar username como documento si es numérico
                    stmt.executeUpdate();
                    System.out.println("✓ Cuenta " + username + " guardada correctamente");
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al guardar cuenta: " + e.getMessage());
        }
    }

    /**
     * ✅ NUEVO: Guarda una cuenta en la base de datos asociada a un usuario por documento
     * Se usa en el registro de nuevos usuarios
     */
    public void guardarCuentaConUsuario(String username, String password, String documento) {
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            MongoDBService.insertarCuenta(username, password, "usuario");
        } else {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO cuentas (username, password, rol, usuario_id) " +
                        "VALUES (?, ?, 'usuario', (SELECT id FROM usuarios WHERE documento = ? LIMIT 1))";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, documento);
                    stmt.executeUpdate();
                    System.out.println("✓ Cuenta " + username + " guardada correctamente para usuario con documento " + documento);
                }
            } catch (SQLException e) {
                System.err.println("✗ Error al guardar cuenta: " + e.getMessage());
            }
        }
    }
}