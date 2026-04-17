package com.example.proyecto.service;

import com.example.proyecto.util.DatabaseConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuentaService {

    private static final File ARCHIVO_CUENTAS = new File(
            System.getProperty("user.dir") + "/data/cuentas.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    public List<CuentaUsuario> cargarCuentas() throws IOException {
        if (!ARCHIVO_CUENTAS.exists()) return new ArrayList<>();
        return mapper.readValue(ARCHIVO_CUENTAS, new TypeReference<List<CuentaUsuario>>() {});
    }

    public void guardarCuentas(List<CuentaUsuario> cuentas) throws IOException {
        ARCHIVO_CUENTAS.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(ARCHIVO_CUENTAS, cuentas);
    }

    public CuentaUsuario buscarCuenta(String username, String password) {
        try {
            List<CuentaUsuario> cuentas = cargarCuentas();
            return cuentas.stream()
                    .filter(c -> c.getUsername().equals(username) && c.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    public void registrarCuenta(CuentaUsuario cuenta) throws IOException {
        List<CuentaUsuario> cuentas = cargarCuentas();
        cuentas.add(cuenta);
        guardarCuentas(cuentas);
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