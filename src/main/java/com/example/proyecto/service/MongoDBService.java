package com.example.proyecto.service;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servicio para conexión y operaciones con MongoDB
 * Proporciona métodos CRUD para las colecciones: usuarios, cuentas y abandonos
 */
public class MongoDBService {
    private static final String CONNECTION_STRING = "mongodb://172.30.16.165:27017";
    private static final String DATABASE_NAME = "gym_db";
    private static MongoClient mongoClient;
    private static MongoDatabase database;

    /**
     * Inicializa la conexión a MongoDB
     */
    public static void conectar() {
        try {
            if (mongoClient == null) {
                mongoClient = MongoClients.create(CONNECTION_STRING);
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("✓ Conexión a MongoDB exitosa");
                crearColeccionesYIndices();
            }
        } catch (Exception e) {
            System.err.println("✗ Error al conectar a MongoDB: " + e.getMessage());
        }
    }

    /**
     * Crea las colecciones e índices necesarios
     */
    private static void crearColeccionesYIndices() {
        try {
            // Crear colecciones si no existen
            MongoIterable<String> colecciones = database.listCollectionNames();
            List<String> listaColecciones = new ArrayList<>();
            colecciones.forEach(listaColecciones::add);

            if (!listaColecciones.contains("usuarios")) {
                database.createCollection("usuarios");
                System.out.println("✓ Colección 'usuarios' creada");
            }

            if (!listaColecciones.contains("cuentas")) {
                database.createCollection("cuentas");
                System.out.println("✓ Colección 'cuentas' creada");
            }

            if (!listaColecciones.contains("abandonos")) {
                database.createCollection("abandonos");
                System.out.println("✓ Colección 'abandonos' creada");
            }

            if (!listaColecciones.contains("membresias")) {
                database.createCollection("membresias");
                System.out.println("✓ Colección 'membresias' creada");
            }

            // Crear índices únicos
            database.getCollection("cuentas").createIndex(new Document("username", 1));
            database.getCollection("usuarios").createIndex(new Document("documento", 1));
            System.out.println("✓ Índices creados");

        } catch (Exception e) {
            System.err.println("⚠ Error al crear colecciones: " + e.getMessage());
        }
    }

    /**
     * Desconecta de MongoDB
     */
    public static void desconectar() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("✓ Desconexión de MongoDB completada");
        }
    }

    // ==================== OPERACIONES USUARIOS ====================

    /**
     * Inserta un nuevo usuario en MongoDB
     */
    public static void insertarUsuario(String nombre, int edad, double peso, double altura,
                                       String objetivo, double calorias, String sexo, String documento) {
        try {
            MongoCollection<Document> collection = database.getCollection("usuarios");
            
            Document usuario = new Document()
                    .append("nombre", nombre)
                    .append("edad", edad)
                    .append("peso", peso)
                    .append("altura", altura)
                    .append("objetivo", objetivo)
                    .append("calorias", calorias)
                    .append("sexo", sexo)
                    .append("documento", documento)
                    .append("abandonado", false)
                    .append("tipoMembresia", "Básica")
                    .append("fechaRegistro", LocalDateTime.now().toString());

            collection.insertOne(usuario);
            System.out.println("✓ Usuario insertado: " + nombre);
        } catch (Exception e) {
            System.err.println("✗ Error al insertar usuario: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los usuarios
     */
    public static List<Document> obtenerTodosUsuarios() {
        List<Document> usuarios = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection("usuarios");
            collection.find().forEach(usuarios::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /**
     * Obtiene un usuario por documento
     */
    public static Document obtenerUsuarioPorDocumento(String documento) {
        try {
            MongoCollection<Document> collection = database.getCollection("usuarios");
            return collection.find(Filters.eq("documento", documento)).first();
        } catch (Exception e) {
            System.err.println("✗ Error al obtener usuario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene un usuario por ID de MongoDB
     */
    public static Document obtenerUsuarioPorId(ObjectId id) {
        try {
            MongoCollection<Document> collection = database.getCollection("usuarios");
            return collection.find(Filters.eq("_id", id)).first();
        } catch (Exception e) {
            System.err.println("✗ Error al obtener usuario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Actualiza un usuario existente
     */
    public static void actualizarUsuario(ObjectId id, String nombre, int edad, double peso,
                                        double altura, String objetivo, double calorias,
                                        String sexo, boolean abandonado, String tipoMembresia) {
        try {
            MongoCollection<Document> collection = database.getCollection("usuarios");
            
            Document actualizacion = new Document()
                    .append("nombre", nombre)
                    .append("edad", edad)
                    .append("peso", peso)
                    .append("altura", altura)
                    .append("objetivo", objetivo)
                    .append("calorias", calorias)
                    .append("sexo", sexo)
                    .append("abandonado", abandonado)
                    .append("tipoMembresia", tipoMembresia);

            collection.updateOne(Filters.eq("_id", id), new Document("$set", actualizacion));
            System.out.println("✓ Usuario actualizado");
        } catch (Exception e) {
            System.err.println("✗ Error al actualizar usuario: " + e.getMessage());
        }
    }

    /**
     * Elimina un usuario
     */
    public static void eliminarUsuario(ObjectId id) {
        try {
            MongoCollection<Document> collection = database.getCollection("usuarios");
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("✓ Usuario eliminado");
        } catch (Exception e) {
            System.err.println("✗ Error al eliminar usuario: " + e.getMessage());
        }
    }

    // ==================== OPERACIONES CUENTAS ====================

    /**
     * Inserta una nueva cuenta
     */
    public static void insertarCuenta(String username, String password, String rol) {
        try {
            MongoCollection<Document> collection = database.getCollection("cuentas");
            
            Document cuenta = new Document()
                    .append("username", username)
                    .append("password", password)
                    .append("rol", rol)
                    .append("fechaCreacion", LocalDateTime.now().toString());

            collection.insertOne(cuenta);
            System.out.println("✓ Cuenta insertada: " + username);
        } catch (Exception e) {
            System.err.println("✗ Error al insertar cuenta: " + e.getMessage());
        }
    }

    /**
     * Obtiene una cuenta por username
     */
    public static Document obtenerCuentaPorUsername(String username) {
        try {
            MongoCollection<Document> collection = database.getCollection("cuentas");
            return collection.find(Filters.eq("username", username)).first();
        } catch (Exception e) {
            System.err.println("✗ Error al obtener cuenta: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene todas las cuentas
     */
    public static List<Document> obtenerTodasCuentas() {
        List<Document> cuentas = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection("cuentas");
            collection.find().forEach(cuentas::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener cuentas: " + e.getMessage());
        }
        return cuentas;
    }

    /**
     * Actualiza una cuenta
     */
    public static void actualizarCuenta(String username, String nuevoPassword, String nuevoRol) {
        try {
            MongoCollection<Document> collection = database.getCollection("cuentas");
            
            Document actualizacion = new Document()
                    .append("password", nuevoPassword)
                    .append("rol", nuevoRol);

            collection.updateOne(Filters.eq("username", username), new Document("$set", actualizacion));
            System.out.println("✓ Cuenta actualizada: " + username);
        } catch (Exception e) {
            System.err.println("✗ Error al actualizar cuenta: " + e.getMessage());
        }
    }

    /**
     * Elimina una cuenta
     */
    public static void eliminarCuenta(String username) {
        try {
            MongoCollection<Document> collection = database.getCollection("cuentas");
            collection.deleteOne(Filters.eq("username", username));
            System.out.println("✓ Cuenta eliminada: " + username);
        } catch (Exception e) {
            System.err.println("✗ Error al eliminar cuenta: " + e.getMessage());
        }
    }

    // ==================== OPERACIONES ABANDONOS ====================

    /**
     * Inserta un abandono
     */
    public static void insertarAbandono(String documento, LocalDate fechaAbandono, String motivo) {
        try {
            MongoCollection<Document> collection = database.getCollection("abandonos");
            
            Document abandono = new Document()
                    .append("documento", documento)
                    .append("fechaAbandono", fechaAbandono.toString())
                    .append("motivo", motivo)
                    .append("fechaRegistro", LocalDateTime.now().toString());

            collection.insertOne(abandono);
            System.out.println("✓ Abandono registrado para: " + documento);
        } catch (Exception e) {
            System.err.println("✗ Error al insertar abandono: " + e.getMessage());
        }
    }

    /**
     * Obtiene todos los abandonos
     */
    public static List<Document> obtenerTodosAbandonos() {
        List<Document> abandonos = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection("abandonos");
            collection.find().forEach(abandonos::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener abandonos: " + e.getMessage());
        }
        return abandonos;
    }

    /**
     * Obtiene abandonos de un usuario
     */
    public static List<Document> obtenerAbandonosPorDocumento(String documento) {
        List<Document> abandonos = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection("abandonos");
            collection.find(Filters.eq("documento", documento)).forEach(abandonos::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener abandonos: " + e.getMessage());
        }
        return abandonos;
    }

    /**
     * Elimina un abandono
     */
    public static void eliminarAbandono(ObjectId id) {
        try {
            MongoCollection<Document> collection = database.getCollection("abandonos");
            collection.deleteOne(Filters.eq("_id", id));
            System.out.println("✓ Abandono eliminado");
        } catch (Exception e) {
            System.err.println("✗ Error al eliminar abandono: " + e.getMessage());
        }
    }

    // ==================== OPERACIONES MEMBRESIAS ====================

    /**
     * Inserta una membresía
     */
    public static void insertarMembresia(String documento, String tipo, LocalDate fechaInicio, LocalDate fechaVencimiento) {
        try {
            MongoCollection<Document> collection = database.getCollection("membresias");
            
            Document membresia = new Document()
                    .append("documento", documento)
                    .append("tipo", tipo)
                    .append("fechaInicio", fechaInicio.toString())
                    .append("fechaVencimiento", fechaVencimiento.toString())
                    .append("estado", "activa");

            collection.insertOne(membresia);
            System.out.println("✓ Membresía insertada para: " + documento);
        } catch (Exception e) {
            System.err.println("✗ Error al insertar membresía: " + e.getMessage());
        }
    }

    /**
     * Obtiene membresía de un usuario
     */
    public static Document obtenerMembresiaPorDocumento(String documento) {
        try {
            MongoCollection<Document> collection = database.getCollection("membresias");
            return collection.find(Filters.eq("documento", documento)).first();
        } catch (Exception e) {
            System.err.println("✗ Error al obtener membresía: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene todas las membresías
     */
    public static List<Document> obtenerTodasMembresias() {
        List<Document> membresias = new ArrayList<>();
        try {
            MongoCollection<Document> collection = database.getCollection("membresias");
            collection.find().forEach(membresias::add);
        } catch (Exception e) {
            System.err.println("✗ Error al obtener membresías: " + e.getMessage());
        }
        return membresias;
    }

    /**
     * Actualiza una membresía
     */
    public static void actualizarMembresia(ObjectId id, String tipo, LocalDate fechaVencimiento, String estado) {
        try {
            MongoCollection<Document> collection = database.getCollection("membresias");
            
            Document actualizacion = new Document()
                    .append("tipo", tipo)
                    .append("fechaVencimiento", fechaVencimiento.toString())
                    .append("estado", estado);

            collection.updateOne(Filters.eq("_id", id), new Document("$set", actualizacion));
            System.out.println("✓ Membresía actualizada");
        } catch (Exception e) {
            System.err.println("✗ Error al actualizar membresía: " + e.getMessage());
        }
    }

    /**
     * Verifica si la base de datos y colecciones existen
     */
    public static void verificarBaseDatos() {
        try {
            if (database == null) {
                System.err.println("✗ No hay conexión a la base de datos");
                return;
            }

            System.out.println("\n=== VERIFICACIÓN DE BASE DE DATOS MONGODB ===");
            System.out.println("Base de datos: " + DATABASE_NAME);

            MongoIterable<String> colecciones = database.listCollectionNames();
            System.out.println("\nColecciones:");
            colecciones.forEach(c -> {
                MongoCollection<Document> col = database.getCollection(c);
                long count = col.countDocuments();
                System.out.println("  - " + c + ": " + count + " documentos");
            });

        } catch (Exception e) {
            System.err.println("✗ Error al verificar base de datos: " + e.getMessage());
        }
    }

    /**
     * Obtiene la instancia de la base de datos
     */
    public static MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Limpia todas las colecciones
     */
    public static void limpiarBaseDatos() {
        try {
            database.getCollection("usuarios").deleteMany(new Document());
            database.getCollection("cuentas").deleteMany(new Document());
            database.getCollection("abandonos").deleteMany(new Document());
            database.getCollection("membresias").deleteMany(new Document());
            System.out.println("✓ Base de datos limpiada");
        } catch (Exception e) {
            System.err.println("✗ Error al limpiar base de datos: " + e.getMessage());
        }
    }
}
