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
     * Guarda una cuenta en la base de datos (sin usuario_id, será admin)
     */
    public void guardarCuenta(String username, String password, String rol) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO cuentas (username, password, rol, email, estado) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                stmt.setString(3, rol);
                stmt.setString(4, username + "@gym.local"); // Email por defecto
                stmt.setString(5, "activa");
                stmt.executeUpdate();
                System.out.println("✓ Cuenta " + username + " guardada correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al guardar cuenta: " + e.getMessage());
        }
    }
}