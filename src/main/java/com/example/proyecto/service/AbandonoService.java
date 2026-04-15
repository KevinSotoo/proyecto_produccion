package com.example.proyecto.service;

import com.example.proyecto.model.Abandono;
import com.example.proyecto.util.DatabaseConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbandonoService {

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.dir") + "/data/abandonos.json"
    );
    private final ObjectMapper mapper;
    private boolean usarBD = true; // Bandera para usar BD

    public AbandonoService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void guardar(List<Abandono> abandonos) throws IOException {
        if (usarBD) {
            guardarEnBD(abandonos);
        } else {
            File parentDir = ARCHIVO_DATOS.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(ARCHIVO_DATOS, abandonos);
        }
    }

    public List<Abandono> cargar() throws IOException {
        if (usarBD) {
            return cargarDeBD();
        } else {
            if (!ARCHIVO_DATOS.exists()) return new ArrayList<>();
            try {
                return mapper.readValue(ARCHIVO_DATOS, new TypeReference<>() {});
            } catch (IOException e) {
                return new ArrayList<>();
            }
        }
    }

    public void agregarAbandono(String nombreUsuario, String motivo) throws IOException {
        if (usarBD) {
            agregarAbandonoBD(nombreUsuario, motivo);
        } else {
            List<Abandono> abandonos = cargar();
            int nuevoId = abandonos.isEmpty() ? 1 : abandonos.stream()
                    .mapToInt(Abandono::getId)
                    .max()
                    .orElse(0) + 1;

            Abandono nuevoAbandono = new Abandono(nuevoId, nombreUsuario, LocalDate.now(), motivo);
            abandonos.add(nuevoAbandono);
            guardar(abandonos);
        }
    }

    // Métodos para trabajar con BD
    private void guardarEnBD(List<Abandono> abandonos) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Limpiar tabla y reinsertarla
            String deleteSQL = "DELETE FROM abandonos";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteSQL);
            }

            for (Abandono a : abandonos) {
                String sql = "INSERT INTO abandonos (usuario_id, fecha_abandono, motivo) SELECT id, ?, ? FROM usuarios WHERE nombre = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDate(1, Date.valueOf(a.getFechaAbandono()));
                    stmt.setString(2, a.getMotivo());
                    stmt.setString(3, a.getNombreUsuario());
                    stmt.executeUpdate();
                }
            }
            System.out.println("✓ Abandonos guardados en BD correctamente");
        } catch (SQLException e) {
            System.out.println("✗ Error al guardar abandonos en BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Abandono> cargarDeBD() {
        List<Abandono> abandonos = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT a.id, u.nombre, a.fecha_abandono, a.motivo FROM abandonos a JOIN usuarios u ON a.usuario_id = u.id ORDER BY a.fecha_abandono DESC";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Abandono abandono = new Abandono(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getDate("fecha_abandono").toLocalDate(),
                            rs.getString("motivo")
                    );
                    abandonos.add(abandono);
                }
            }
            System.out.println("✓ Abandonos cargados desde BD: " + abandonos.size() + " registros");
        } catch (SQLException e) {
            System.out.println("✗ Error al cargar abandonos desde BD: " + e.getMessage());
            System.out.println("⚠ Intentando cargar desde JSON...");
            try {
                if (ARCHIVO_DATOS.exists()) {
                    abandonos = mapper.readValue(ARCHIVO_DATOS, new TypeReference<>() {});
                    usarBD = false; // Cambiar a JSON si BD no está disponible
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return abandonos;
    }

    private void agregarAbandonoBD(String nombreUsuario, String motivo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO abandonos (usuario_id, fecha_abandono, motivo) SELECT id, ?, ? FROM usuarios WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(LocalDate.now()));
                stmt.setString(2, motivo);
                stmt.setString(3, nombreUsuario);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ Abandono registrado en BD");
                }
            }
        } catch (SQLException e) {
            System.out.println("✗ Error al agregar abandono en BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void eliminarAbandono(int abandonoId) {
        if (usarBD) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM abandonos WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, abandonoId);
                    stmt.executeUpdate();
                    System.out.println("✓ Abandono eliminado de BD");
                }
            } catch (SQLException e) {
                System.out.println("✗ Error al eliminar abandono: " + e.getMessage());
            }
        }
    }

    /**
     * Guarda un único abandono
     */
    public void guardar(Abandono abandono) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO abandonos (usuario_id, fecha_abandono, motivo) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, abandono.getUsuarioId());
                stmt.setDate(2, Date.valueOf(abandono.getFechaAbandono()));
                stmt.setString(3, abandono.getMotivo());
                stmt.executeUpdate();
                System.out.println("✓ Abandono guardado correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al guardar abandono: " + e.getMessage());
        }
    }
}




