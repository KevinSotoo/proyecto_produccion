package com.example.proyecto;

import com.example.proyecto.service.MembresiaService;
import com.example.proyecto.service.TimeService;
import com.example.proyecto.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;

public class ActualizadorMembresias {
    public static void main(String[] args) {
        System.out.println("=== ACTUALIZADOR DE MEMBRESÍAS ===\n");

        LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
        LocalDate nuevaFechaVencimiento = hoyServidor.plusDays(30);

        System.out.println("Fecha hoy (servidor): " + hoyServidor);
        System.out.println("Nueva fecha de vencimiento: " + nuevaFechaVencimiento);
        System.out.println();

        actualizarSQLite(nuevaFechaVencimiento);
        System.out.println();
    }

    private static void actualizarSQLite(LocalDate fechaVencimiento) {
        String url = "jdbc:sqlite:D:/Projects intellij/gym_db.db";
        String sql = "UPDATE membresias SET fecha_vencimiento = ?, estado = 'activa'";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(fechaVencimiento));
            int filasActualizadas = pstmt.executeUpdate();

            System.out.println("✓ SQLite: " + filasActualizadas + " membresías actualizadas");
            System.out.println("✓ Nueva fecha de vencimiento: " + fechaVencimiento);

            // Verificar
            verificarActualizacion(conn, fechaVencimiento);

        } catch (SQLException e) {
            System.out.println("✗ Error al actualizar SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verificarActualizacion(Connection conn, LocalDate fechaVencimiento) throws SQLException {
        String sqlVerificar = "SELECT COUNT(*) as total, " +
                "SUM(CASE WHEN fecha_vencimiento >= ? THEN 1 ELSE 0 END) as activas " +
                "FROM membresias";

        try (PreparedStatement pstmt = conn.prepareStatement(sqlVerificar)) {
            // Usar la fecha del servidor para la verificación
            pstmt.setDate(1, Date.valueOf(TimeService.obtenerFechaDelServidor()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int activas = rs.getInt("activas");
                    System.out.println("\n=== VERIFICACIÓN ===");
                    System.out.println("Total de membresías: " + total);
                    System.out.println("Membresías activas: " + activas);
                }
            }
        }
    }
}

