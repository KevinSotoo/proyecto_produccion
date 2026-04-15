package com.example.proyecto;

/**
 * EJEMPLO DE USO - Importador de Datos JSON
 * 
 * Este archivo muestra 3 formas diferentes de importar tus datos JSON a la BD
 */

import com.example.proyecto.util.JsonImporter;

public class EjemploImportacion {

    // ✅ FORMA 1: Importar TODO de una vez
    public static void ejemplo1_ImportarTodo() {
        System.out.println("\n=== EJEMPLO 1: Importar TODO ===");
        JsonImporter.importarTodo();
    }

    // ✅ FORMA 2: Importar por tipo específico
    public static void ejemplo2_ImportarPorTipo() {
        System.out.println("\n=== EJEMPLO 2: Importar por tipo ===");
        
        // Solo usuarios
        JsonImporter.importarUsuarios("data/gimnasio_usuarios.json");
        
        // Solo abandonos
        JsonImporter.importarAbandonos("data/abandonos.json");
        
        // Solo cuentas
        JsonImporter.importarCuentas("data/cuentas.json");
    }

    // ✅ FORMA 3: Usar servicios directamente
    public static void ejemplo3_UsarServiciosDirectamente() {
        System.out.println("\n=== EJEMPLO 3: Usar servicios directamente ===");
        
        // Ejemplo con usuario
        /*
        Usuario usuario = new Usuario(
            0,                          // ID (será generado por BD)
            "1234567890",              // documento
            "Juan Pérez",              // nombre
            25,                        // edad
            "Masculino",               // sexo
            75.5,                      // peso
            1.80,                      // altura
            "Ganar masa muscular",     // objetivo
            2800.0,                    // calorías
            null                       // tipo_membresia
        );
        
        UsuarioService usuarioService = new UsuarioService();
        usuarioService.guardar(usuario);
        */
    }

    public static void main(String[] args) {
        System.out.println("\n" +
            "╔════════════════════════════════════════════════════════╗\n" +
            "║   EJEMPLOS DE IMPORTACIÓN DE DATOS JSON → BD          ║\n" +
            "╚════════════════════════════════════════════════════════╝");

        // Descomentar la forma que quieras usar:
        
        ejemplo1_ImportarTodo();
        // ejemplo2_ImportarPorTipo();
        // ejemplo3_UsarServiciosDirectamente();

        System.out.println("\n✓ Ejemplo completado\n");
    }
}

