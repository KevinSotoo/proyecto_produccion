package com.example.proyecto;

import com.example.proyecto.util.JsonImporter;

/**
 * Utilidad para importar datos desde archivos JSON a la base de datos
 * Úsalo desde línea de comandos:
 * 
 * java -cp target/classes:target/dependency/* com.example.proyecto.ImportadorDatos
 */
public class ImportadorDatos {
    public static void main(String[] args) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("   IMPORTADOR DE DATOS - JSON → BASE DE DATOS");
        System.out.println("════════════════════════════════════════════════════════\n");

        if (args.length == 0) {
            System.out.println("Opciones disponibles:");
            System.out.println("  todo      - Importa todos los datos (usuarios, abandonos, cuentas)");
            System.out.println("  usuarios  - Importa solo usuarios desde gimnasio_usuarios.json");
            System.out.println("  abandonos - Importa solo abandonos desde abandonos.json");
            System.out.println("  cuentas   - Importa solo cuentas desde cuentas.json");
            System.out.println("\nEjemplo: java com.example.proyecto.ImportadorDatos todo");
            return;
        }

        String opcion = args[0].toLowerCase();

        try {
            switch (opcion) {
                case "todo":
                    JsonImporter.importarTodo();
                    break;
                case "usuarios":
                    JsonImporter.importarUsuarios("data/gimnasio_usuarios.json");
                    break;
                case "abandonos":
                    JsonImporter.importarAbandonos("data/abandonos.json");
                    break;
                case "cuentas":
                    JsonImporter.importarCuentas("data/cuentas.json");
                    break;
                default:
                    System.out.println("✗ Opción no reconocida: " + opcion);
                    System.out.println("Usa: todo, usuarios, abandonos o cuentas");
            }
        } catch (Exception e) {
            System.err.println("✗ Error durante la importación: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n════════════════════════════════════════════════════════\n");
    }
}

