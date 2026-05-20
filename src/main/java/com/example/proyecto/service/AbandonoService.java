package com.example.proyecto.service;

import com.example.proyecto.model.Abandono;
import com.example.proyecto.util.DatabaseConnection;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import com.example.proyecto.service.TimeService;
import java.util.ArrayList;
import java.util.List;

public class AbandonoService {

    // Eliminado manejo de JSON local. Abandonos se gestionan en la BD o MongoDB.

    public void guardar(List<Abandono> abandonos) throws IOException {
        guardarEnBD(abandonos);
    }

    public List<Abandono> cargar() throws IOException {
        return cargarDeBD();
    }

    public void agregarAbandono(String nombreUsuario, String motivo) throws IOException {
        agregarAbandonoBD(nombreUsuario, motivo);
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
            e.printStackTrace();
        }
        return abandonos;
    }

    private void agregarAbandonoBD(String nombreUsuario, String motivo) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO abandonos (usuario_id, fecha_abandono, motivo) SELECT id, ?, ? FROM usuarios WHERE nombre = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDate(1, Date.valueOf(TimeService.obtenerFechaDelServidor()));
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




