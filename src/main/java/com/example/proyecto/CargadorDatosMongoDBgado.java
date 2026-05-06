package com.example.proyecto;

import com.example.proyecto.service.MongoDBService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Clase para cargar datos JSON a MongoDB
 * Lee los archivos JSON y los inserta en las colecciones correspondientes
 */
public class CargadorDatosMongoDBgado {

    private static final String DATA_PATH = "data/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("=== CARGADOR DE DATOS A MONGODB ===\n");
        
        // Conectar a MongoDB
        MongoDBService.conectar();
        
        // Limpiar datos previos (opcional)
        System.out.println("\n¿Deseas limpiar los datos existentes? (S/N)");
        try {
            int opcion = System.in.read();
            if (opcion == 'S' || opcion == 's') {
                MongoDBService.limpiarBaseDatos();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Cargar datos
        System.out.println("\nCargando datos...\n");
        cargarDatosUsuarios();
        cargarDatosCuentas();
        cargarDatosAbandonos();
        cargarDatosMembresias();
        
        // Verificar
        MongoDBService.verificarBaseDatos();
        
        // Desconectar
        MongoDBService.desconectar();
        System.out.println("\n✓ Proceso completado");
    }

    /**
     * Carga usuarios desde gimnasio_usuarios.json
     */
    private static void cargarDatosUsuarios() {
        try {
            File file = new File(DATA_PATH + "gimnasio_usuarios.json");
            List<Map<String, Object>> usuarios = objectMapper.readValue(file, List.class);
            
            System.out.println("Insertando " + usuarios.size() + " usuarios...");
            
            for (Map<String, Object> usuario : usuarios) {
                String nombre = (String) usuario.get("nombre");
                Integer edad = (Integer) usuario.get("edad");
                Double peso = ((Number) usuario.get("peso")).doubleValue();
                Double altura = ((Number) usuario.get("altura")).doubleValue();
                String objetivo = (String) usuario.get("objetivo");
                Double calorias = ((Number) usuario.get("calorias")).doubleValue();
                String sexo = (String) usuario.get("sexo");
                String documento = (String) usuario.get("documento");
                
                MongoDBService.insertarUsuario(nombre, edad, peso, altura, objetivo, calorias, sexo, documento);
            }
            
            System.out.println("✓ Usuarios cargados exitosamente\n");
        } catch (Exception e) {
            System.err.println("✗ Error al cargar usuarios: " + e.getMessage() + "\n");
        }
    }

    /**
     * Carga cuentas desde cuentas.json
     */
    private static void cargarDatosCuentas() {
        try {
            File file = new File(DATA_PATH + "cuentas.json");
            List<Map<String, Object>> cuentas = objectMapper.readValue(file, List.class);
            
            System.out.println("Insertando " + cuentas.size() + " cuentas...");
            
            for (Map<String, Object> cuenta : cuentas) {
                String username = (String) cuenta.get("username");
                String password = (String) cuenta.get("password");
                String rol = (String) cuenta.get("rol");
                
                MongoDBService.insertarCuenta(username, password, rol);
            }
            
            System.out.println("✓ Cuentas cargadas exitosamente\n");
        } catch (Exception e) {
            System.err.println("✗ Error al cargar cuentas: " + e.getMessage() + "\n");
        }
    }

    /**
     * Carga abandonos desde abandonos.json
     */
    private static void cargarDatosAbandonos() {
        try {
            File file = new File(DATA_PATH + "abandonos.json");
            List<Map<String, Object>> abandonos = objectMapper.readValue(file, List.class);
            
            System.out.println("Insertando " + abandonos.size() + " abandonos...");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (Map<String, Object> abandono : abandonos) {
                String documento = (String) abandono.get("documento");
                String fechaStr = (String) abandono.get("fechaAbandono");
                LocalDate fecha = LocalDate.parse(fechaStr, formatter);
                String motivo = (String) abandono.get("motivo");
                
                MongoDBService.insertarAbandono(documento, fecha, motivo);
            }
            
            System.out.println("✓ Abandonos cargados exitosamente\n");
        } catch (Exception e) {
            System.err.println("✗ Error al cargar abandonos: " + e.getMessage() + "\n");
        }
    }

    /**
     * Carga membresías desde membresias.json
     */
    private static void cargarDatosMembresias() {
        try {
            File file = new File(DATA_PATH + "membresias.json");
            List<Map<String, Object>> membresias = objectMapper.readValue(file, List.class);
            
            System.out.println("Insertando " + membresias.size() + " membresías...");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (Map<String, Object> membresia : membresias) {
                String documento = (String) membresia.get("documento");
                String tipo = (String) membresia.get("tipoMembresia");
                String fechaInicioStr = (String) membresia.get("fechaInicio");
                String fechaVencimientoStr = (String) membresia.get("fechaVencimiento");
                
                LocalDate fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
                LocalDate fechaVencimiento = LocalDate.parse(fechaVencimientoStr, formatter);
                
                MongoDBService.insertarMembresia(documento, tipo, fechaInicio, fechaVencimiento);
            }
            
            System.out.println("✓ Membresías cargadas exitosamente\n");
        } catch (Exception e) {
            System.err.println("✗ Error al cargar membresías: " + e.getMessage() + "\n");
        }
    }
}

