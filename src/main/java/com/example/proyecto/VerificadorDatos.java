package com.example.proyecto;

import com.example.proyecto.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Herramienta para verificar que los datos se están insertando correctamente en MySQL
 * Úsalo desde línea de comandos:
 *
 * java -cp target/classes:target/dependency/* com.example.proyecto.VerificadorDatos
 */
public class VerificadorDatos {

    public static void main(String[] args) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("   VERIFICADOR DE DATOS - BASE DE DATOS");
        System.out.println("════════════════════════════════════════════════════════\n");

        try {
            // Probar conexión
            System.out.println("1️⃣  PROBANDO CONEXIÓN A LA BASE DE DATOS...");
            DatabaseConnection.testConnection();
            System.out.println("   " + DatabaseConnection.getConnectionInfo());
            System.out.println();

            // Verificar datos
            verificarDatos();

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n════════════════════════════════════════════════════════\n");
    }

    public static void verificarDatos() {
        try (Connection conn = DatabaseConnection.getConnection()) {

            // Contar usuarios
            System.out.println("2️⃣  VERIFICANDO TABLA: usuarios");
            System.out.println("   ─────────────────────────────────");
            long countUsuarios = contarRegistros(conn, "usuarios");
            System.out.println("   Total de usuarios: " + countUsuarios);
            if (countUsuarios > 0) {
                mostrarPrimerosRegistros(conn, "usuarios", 3);
            }
            System.out.println();

            // Contar abandonos
            System.out.println("3️⃣  VERIFICANDO TABLA: abandonos");
            System.out.println("   ─────────────────────────────────");
            long countAbandonos = contarRegistros(conn, "abandonos");
            System.out.println("   Total de abandonos: " + countAbandonos);
            if (countAbandonos > 0) {
                mostrarPrimerosRegistros(conn, "abandonos", 3);
            }
            System.out.println();

            // Contar cuentas
            System.out.println("4️⃣  VERIFICANDO TABLA: cuentas");
            System.out.println("   ─────────────────────────────────");
            long countCuentas = contarRegistros(conn, "cuentas");
            System.out.println("   Total de cuentas: " + countCuentas);
            if (countCuentas > 0) {
                mostrarPrimerosRegistros(conn, "cuentas", 3);
            }
            System.out.println();

            // Verificar membresias si existen
            System.out.println("5️⃣  VERIFICANDO TABLA: membresias");
            System.out.println("   ─────────────────────────────────");
            try {
                long countMembresias = contarRegistros(conn, "membresias");
                System.out.println("   Total de membresias: " + countMembresias);
                if (countMembresias > 0) {
                    mostrarPrimerosRegistros(conn, "membresias", 3);
                }
            } catch (Exception e) {
                System.out.println("   ⚠️  Tabla membresias no encontrada o no accesible");
            }
            System.out.println();

            // Resumen
            System.out.println("📊 RESUMEN DE DATOS:");
            System.out.println("   ─────────────────────────────────");
            System.out.println("   ✓ Usuarios:  " + countUsuarios);
            System.out.println("   ✓ Abandonos: " + countAbandonos);
            System.out.println("   ✓ Cuentas:   " + countCuentas);
            System.out.println();

            if (countUsuarios > 0 && countCuentas > 0) {
                System.out.println("✅ Los datos se están insertando correctamente en la base de datos");
            } else if (countUsuarios == 0 && countCuentas == 0 && countAbandonos == 0) {
                System.out.println("⚠️  NO HAY DATOS EN LA BASE DE DATOS");
                System.out.println("   Ejecuta: java com.example.proyecto.ImportadorDatos todo");
            } else {
                System.out.println("⚠️  ALGUNOS DATOS NO SE HAN INSERTADO");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al verificar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static long contarRegistros(Connection conn, String tabla) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT COUNT(*) as total FROM " + tabla;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getLong("total");
            }
        } catch (Exception e) {
            System.err.println("   ❌ Error al contar: " + e.getMessage());
        }
        return 0;
    }

    private static void mostrarPrimerosRegistros(Connection conn, String tabla, int limite) {
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM " + tabla + " LIMIT " + limite;
            ResultSet rs = stmt.executeQuery(sql);

            int count = 0;
            while (rs.next() && count < limite) {
                switch (tabla.toLowerCase()) {
                    case "usuarios":
                        System.out.println("   - ID: " + rs.getInt("id") +
                                         ", Nombre: " + rs.getString("nombre") +
                                         ", Documento: " + rs.getString("documento"));
                        break;
                    case "abandonos":
                        System.out.println("   - ID: " + rs.getInt("id") +
                                         ", Usuario ID: " + rs.getInt("usuario_id") +
                                         ", Fecha: " + rs.getDate("fecha_abandono") +
                                         ", Motivo: " + rs.getString("motivo"));
                        break;
                    case "cuentas":
                        System.out.println("   - ID: " + rs.getInt("id") +
                                         ", Username: " + rs.getString("username") +
                                         ", Rol: " + rs.getString("rol"));
                        break;
                    case "membresias":
                        System.out.println("   - ID: " + rs.getInt("id") +
                                         ", Nombre: " + rs.getString("nombre") +
                                         ", Precio: " + rs.getDouble("precio"));
                        break;
                }
                count++;
            }
        } catch (Exception e) {
            System.err.println("   ❌ Error al mostrar registros: " + e.getMessage());
        }
    }
}

