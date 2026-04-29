package com.example.proyecto;

import com.example.proyecto.service.TimeService;
import com.example.proyecto.service.MembresiaService;

/**
 * Ejemplo de uso de la World Time API
 * Demuestra cómo validar membresías usando la fecha del servidor
 */
public class EjemploWorldTimeAPI {
    public static void main(String[] args) {
        System.out.println("=== EJEMPLO: World Time API Integration ===\n");

        // 1. Obtener la fecha del servidor
        System.out.println("1. Obteniendo fecha del servidor...");
        try {
            var fechaServidor = TimeService.obtenerFechaDelServidor();
            System.out.println("   ✓ Fecha del servidor: " + fechaServidor);
        } catch (Exception e) {
            System.out.println("   ✗ Error: " + e.getMessage());
        }

        // 2. Obtener la hora del servidor
        System.out.println("\n2. Obteniendo hora del servidor...");
        try {
            var horaServidor = TimeService.obtenerHoraDelServidor();
            System.out.println("   ✓ Hora del servidor: " + horaServidor);
        } catch (Exception e) {
            System.out.println("   ✗ Error: " + e.getMessage());
        }

        // 3. Obtener zona horaria
        System.out.println("\n3. Obteniendo zona horaria del servidor...");
        try {
            var zonaHoraria = TimeService.obtenerZonaHoraria();
            System.out.println("   ✓ Zona horaria: " + zonaHoraria);
        } catch (Exception e) {
            System.out.println("   ✗ Error: " + e.getMessage());
        }

        // 4. Validar membresía activa
        System.out.println("\n4. Validando membresía con ID 1...");
        MembresiaService membresiaService = new MembresiaService();
        boolean esActiva = membresiaService.validarMembresiaActiva(1);
        System.out.println("   ✓ ¿Membresía activa?: " + (esActiva ? "SÍ" : "NO"));

        // 5. Obtener membresías activas del usuario
        System.out.println("\n5. Obteniendo membresías activas del usuario 1...");
        try {
            var membresiasActivas = membresiaService.obtenerMembresiasActivasDelUsuario(1);
            System.out.println("   ✓ Total de membresías activas: " + membresiasActivas.size());
            for (var m : membresiasActivas) {
                System.out.println("     - " + m.getTipoMembresia() + " (Vencimiento: " + m.getFechaVencimiento() + ")");
            }
        } catch (Exception e) {
            System.out.println("   ✗ Error: " + e.getMessage());
        }

        // 6. Información del servidor
        System.out.println("\n6. Información de sincronización:");
        String infoServidor = membresiaService.obtenerInfoServidor();
        System.out.println("   " + infoServidor);

        System.out.println("\n=== FIN DEL EJEMPLO ===");
    }
}

