package com.example.proyecto.util;

import com.example.proyecto.model.Usuario;
import com.example.proyecto.model.Abandono;
import com.example.proyecto.service.UsuarioService;
import com.example.proyecto.service.AbandonoService;
import com.example.proyecto.service.CuentaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonImporter {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Carga usuarios desde un archivo JSON e los inserta en la base de datos
     */
    public static void importarUsuarios(String jsonPath) {
        try {
            UsuarioService usuarioService = new UsuarioService();
            JsonNode root = mapper.readTree(new File(jsonPath));
            
            for (JsonNode node : root) {
                // Convertir altura de cm a metros si es necesario (si es > 10)
                double altura = node.get("altura").asDouble();
                if (altura > 10) {
                    altura = altura / 100.0; // Convertir de cm a metros
                }
                
                Usuario usuario = new Usuario(
                    node.get("nombre").asText(),
                    node.get("edad").asInt(),
                    node.get("peso").asDouble(),
                    altura,
                    node.get("objetivo").asText(),
                    node.get("calorias").asDouble(),
                    node.get("sexo").asText(),
                    node.get("documento").asText()
                );
                usuarioService.guardar(usuario);
            }
            System.out.println("✓ Usuarios importados exitosamente desde " + jsonPath);
        } catch (Exception e) {
            System.err.println("✗ Error al importar usuarios: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga abandonos desde un archivo JSON e los inserta en la base de datos
     * Nota: El JSON contiene "documento", buscaremos el ID por documento
     */
    public static void importarAbandonos(String jsonPath) {
        try {
            AbandonoService abandonoService = new AbandonoService();
            UsuarioService usuarioService = new UsuarioService();
            JsonNode root = mapper.readTree(new File(jsonPath));
            
            for (JsonNode node : root) {
                String documento = node.get("documento").asText();
                int usuarioId = usuarioService.obtenerIdPorDocumento(documento);
                
                if (usuarioId > 0) {
                    Abandono abandono = new Abandono(
                        0, // ID será generado por la BD
                        usuarioId,
                        LocalDate.parse(node.get("fechaAbandono").asText()),
                        node.get("motivo").asText()
                    );
                    abandonoService.guardar(abandono);
                } else {
                    System.err.println("⚠ No se encontró usuario con documento: " + documento);
                }
            }
            System.out.println("✓ Abandonos importados exitosamente desde " + jsonPath);
        } catch (Exception e) {
            System.err.println("✗ Error al importar abandonos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Carga cuentas desde un archivo JSON e los inserta en la base de datos
     * Nota: El JSON no tiene un ID de usuario directo, se intenta por username
     */
    public static void importarCuentas(String jsonPath) {
        try {
            CuentaService cuentaService = new CuentaService();
            JsonNode root = mapper.readTree(new File(jsonPath));
            
            for (JsonNode node : root) {
                String username = node.get("username").asText();
                String password = node.get("password").asText();
                String rol = node.get("rol").asText();
                
                cuentaService.guardarCuenta(username, password, rol);
            }
            System.out.println("✓ Cuentas importadas exitosamente desde " + jsonPath);
        } catch (Exception e) {
            System.err.println("✗ Error al importar cuentas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Importa todos los datos desde los archivos JSON
     */
    public static void importarTodo() {
        String basePath = "data/";
        
        System.out.println("\n=== Iniciando importación de datos desde JSON ===");
        importarUsuarios(basePath + "gimnasio_usuarios.json");
        importarAbandonos(basePath + "abandonos.json");
        importarCuentas(basePath + "cuentas.json");
        System.out.println("=== Importación completada ===\n");
    }
}

