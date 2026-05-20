package com.example.proyecto;

import com.example.proyecto.service.TimeService;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase de prueba para verificar si la API de fecha se está ejecutando correctamente
 * Ejecuta esto desde IntelliJ (Run) o con Maven:
 * mvn -Dexec.mainClass="com.example.proyecto.TestTimeServiceDirect" exec:java
 */
public class TestTimeServiceDirect {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("PRUEBA DE TIMESERVICE - API DE FECHA");
        System.out.println("========================================\n");

        System.out.println("1. Intentando obtener FECHA del servidor...");
        try {
            LocalDate fecha = TimeService.obtenerFechaDelServidor();
            System.out.println("✓ ÉXITO - Fecha obtenida: " + fecha);
            System.out.println("  → Tipo: " + fecha.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n2. Intentando obtener HORA del servidor...");
        try {
            LocalDateTime hora = TimeService.obtenerHoraDelServidor();
            System.out.println("✓ ÉXITO - Hora obtenida: " + hora);
            System.out.println("  → Tipo: " + hora.getClass().getSimpleName());
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n3. Intentando obtener ZONA HORARIA...");
        try {
            String zona = TimeService.obtenerZonaHoraria();
            System.out.println("✓ ÉXITO - Zona horaria: " + zona);
        } catch (Exception e) {
            System.out.println("✗ ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n========================================");
        System.out.println("PRUEBA COMPLETADA");
        System.out.println("========================================\n");

        System.out.println("Comparación local vs servidor:");
        System.out.println("  • LocalDate.now() (local):        " + LocalDate.now());
        System.out.println("  • LocalDateTime.now() (local):    " + LocalDateTime.now());
        System.out.println("  • TimeService (servidor/API):     " + TimeService.obtenerFechaDelServidor());
    }
}

