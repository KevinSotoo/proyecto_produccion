package com.example.proyecto.service;

import org.bson.Document;
import com.mongodb.client.MongoCollection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditService {

    /**
     * Registra una operación de auditoría en MongoDB
     * Solo se guarda en Audit si se está usando MongoDB
     * @param operacion Tipo de operación (INSERT, UPDATE, DELETE)
     * @param tabla Tabla/Colección afectada
     * @param detalles Descripción detallada de la operación
     * @param datosAfectados Datos que se modificaron/eliminaron/agregaron
     */
    public static void registrarOperacion(String operacion, String tabla, String detalles, String datosAfectados) {
        try {
            MongoDBService.conectar();
            MongoCollection<Document> collection = MongoDBService.getDatabase().getCollection("Registro");

            Document registro = new Document()
                    .append("timestamp", LocalDateTime.now().toString())
                    .append("operacion", operacion)
                    .append("tabla", tabla)
                    .append("detalles", detalles)
                    .append("datosAfectados", datosAfectados)
                    .append("ip", obtenerIPLocal())
                    .append("usuario", System.getProperty("user.name"));

            collection.insertOne(registro);
            System.out.println("✓ Operación registrada en auditoría: " + operacion + " en " + tabla);
        } catch (Exception e) {
            System.err.println("⚠ Error al registrar en auditoría: " + e.getMessage());
        }
    }

    /**
     * Registra una inserción
     */
    public static void registrarInsercion(String tabla, String detalles) {
        registrarOperacion("INSERT", tabla, detalles, "");
    }

    /**
     * Registra una modificación
     */
    public static void registrarModificacion(String tabla, String detalles, String datosAntiguos) {
        registrarOperacion("UPDATE", tabla, detalles, "Anterior: " + datosAntiguos);
    }

    /**
     * Registra una eliminación
     */
    public static void registrarEliminacion(String tabla, String detalles) {
        registrarOperacion("DELETE", tabla, detalles, "");
    }

    /**
     * Obtiene todos los registros de auditoría
     */
    public static List<Document> obtenerTodosRegistros() {
        List<Document> registros = new ArrayList<>();
        try {
            MongoDBService.conectar();
            MongoCollection<Document> collection = MongoDBService.getDatabase().getCollection("Registro");
            collection.find().forEach(registros::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener registros de auditoría: " + e.getMessage());
        }
        return registros;
    }

    /**
     * Obtiene registros de auditoría por tabla
     */
    public static List<Document> obtenerRegistrosPorTabla(String tabla) {
        List<Document> registros = new ArrayList<>();
        try {
            MongoDBService.conectar();
            MongoCollection<Document> collection = MongoDBService.getDatabase().getCollection("Registro");
            collection.find(new Document("tabla", tabla)).forEach(registros::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener registros de auditoría: " + e.getMessage());
        }
        return registros;
    }

    private static String obtenerIPLocal() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}




