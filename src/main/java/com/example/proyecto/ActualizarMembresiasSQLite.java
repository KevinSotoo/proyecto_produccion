package com.example.proyecto;

import java.sql.*;

/**
 * Utilidad para actualizar membresías en SQLite sin dependencias de TimeService.
 * Este script ignora los fallos de API y actualiza directamente la BD.
 *
 * Ejecuta desde IntelliJ: Run → ActualizarMembresiasSQLite
 */
public class ActualizarMembresiasSQLite {

    public static void main(String[] args) {
        // Hoy es 2026-05-19, vencimiento será 2026-06-18 (30 días después)
        String DB_PATH = "jdbc:sqlite:D:/Projects intellij/gym_db.db";
        String HOY = "2026-05-19";
        String VENCIMIENTO = "2026-06-18";

        System.out.println("=".repeat(50));
        System.out.println("ACTUALIZADOR DE MEMBRESÍAS - SQLite");
        System.out.println("=".repeat(50));
        System.out.println();
        System.out.println("📅 Fecha hoy (local):           " + HOY);
        System.out.println("📅 Nueva fecha vencimiento:     " + VENCIMIENTO);
        System.out.println();

        try {
            // Cargar driver SQLite
            Class.forName("org.sqlite.JDBC");

            Connection conn = DriverManager.getConnection(DB_PATH);
            System.out.println("✓ Conectado a SQLite");

            // Información ANTES
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM membresias");
                if (rs.next()) {
                    int total = rs.getInt(1);
                    System.out.println("\nTotal de membresías en BD: " + total);
                }
            }

            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM membresias WHERE fecha_vencimiento >= '" + HOY + "'"
                );
                if (rs.next()) {
                    int activas = rs.getInt(1);
                    System.out.println("Membresías activas (antes del update): " + activas);
                }
            }

            // ACTUALIZAR
            System.out.println("\n⏳ Actualizando membresías...");
            try (Statement stmt = conn.createStatement()) {
                String updateSQL = "UPDATE membresias SET " +
                    "fecha_vencimiento = '" + VENCIMIENTO + "', " +
                    "fecha_inicio = '" + HOY + "', " +
                    "estado = 'activa'";

                int filasAfectadas = stmt.executeUpdate(updateSQL);
                System.out.println("✓ Membresías actualizadas: " + filasAfectadas);
            }

            // Información DESPUÉS
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM membresias WHERE fecha_vencimiento >= '" + HOY + "'"
                );
                if (rs.next()) {
                    int activas = rs.getInt(1);
                    System.out.println("Membresías activas (después del update): " + activas);
                }
            }

            // Mostrar muestra
            System.out.println("\nPrimeras 3 membresías:");
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT id, usuario_id, tipo_membresia, fecha_inicio, " +
                    "fecha_vencimiento, estado FROM membresias LIMIT 3"
                );
                while (rs.next()) {
                    System.out.println("  • ID: " + rs.getInt(1) +
                        ", Usuario: " + rs.getInt(2) +
                        ", Tipo: " + rs.getString(3));
                    System.out.println("    Inicio: " + rs.getString(4) +
                        ", Vencimiento: " + rs.getString(5) +
                        ", Estado: " + rs.getString(6));
                }
            }

            conn.close();
            System.out.println("\n✓ Cambios guardados");
            System.out.println("=".repeat(50));

        } catch (ClassNotFoundException e) {
            System.out.println("✗ Error: Driver SQLite no encontrado");
            System.out.println("  Asegúrate de que la dependencia sqlite-jdbc está en pom.xml");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("✗ Error SQL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

