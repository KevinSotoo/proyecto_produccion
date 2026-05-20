package com.example.proyecto.service;

import com.example.proyecto.model.Membresia;
import com.example.proyecto.util.DatabaseConnection;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MembresiaService {
    private static final Logger logger = Logger.getLogger(MembresiaService.class.getName());
    
    // ...existing code...
    
    public List<Membresia> cargar() {
        List<Membresia> membresias = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM membresias";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    try {
                        Membresia m = new Membresia(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),
                            rs.getString("tipo_membresia"),
                            LocalDate.parse(rs.getString("fecha_inicio")),
                            LocalDate.parse(rs.getString("fecha_vencimiento")),
                            rs.getDouble("precio"),
                            rs.getString("estado"),
                            rs.getString("fecha_registro")
                        );
                        membresias.add(m);
                    } catch (Exception dateEx) {
                        logger.log(Level.SEVERE, "Error al parsear fechas de membresía: " + dateEx.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al cargar membresías", e);
        }
        return membresias;
    }

    public void guardar(Membresia m) {
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            MongoDBService.insertarMembresia(m.getUsuarioId() + "", m.getTipoMembresia(), m.getFechaInicio(), m.getFechaVencimiento());
        } else {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO membresias (usuario_id, tipo_membresia, fecha_inicio, fecha_vencimiento, precio, estado) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, m.getUsuarioId());
                    stmt.setString(2, m.getTipoMembresia());
                    stmt.setDate(3, Date.valueOf(m.getFechaInicio()));
                    stmt.setDate(4, Date.valueOf(m.getFechaVencimiento()));
                    stmt.setDouble(5, m.getPrecio());
                    stmt.setString(6, m.getEstado());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al guardar membresía", e);
            }
        }
    }

    public void actualizar(Membresia m) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE membresias SET tipo_membresia=?, fecha_inicio=?, fecha_vencimiento=?, precio=?, estado=? WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, m.getTipoMembresia());
                stmt.setDate(2, Date.valueOf(m.getFechaInicio()));
                stmt.setDate(3, Date.valueOf(m.getFechaVencimiento()));
                stmt.setDouble(4, m.getPrecio());
                stmt.setString(5, m.getEstado());
                stmt.setInt(6, m.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar membresía", e);
        }
    }

    public void eliminar(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "DELETE FROM membresias WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar membresía", e);
        }
    }

    /**
     * Valida si una membresía está activa usando la fecha del servidor (World Time API)
     * @param membresiaId ID de la membresía a validar
     * @return true si la membresía está activa, false si está vencida
     */
    public boolean validarMembresiaActiva(int membresiaId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT fecha_vencimiento FROM membresias WHERE id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, membresiaId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        try {
                            LocalDate fechaVencimiento = LocalDate.parse(rs.getString("fecha_vencimiento"));
                            LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
                            return fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor);
                        } catch (Exception dateEx) {
                            logger.log(Level.SEVERE, "Error al parsear fecha: " + dateEx.getMessage());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al validar membresía activa", e);
        }
        return false;
    }

    /**
     * Obtiene todas las membresías activas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de membresías activas
     */
    public List<Membresia> obtenerMembresiasActivasDelUsuario(int usuarioId) {
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            List<Membresia> membresiasActivas = new ArrayList<>();
            LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
            Document doc = MongoDBService.obtenerMembresiaPorDocumento(usuarioId + "");
            if (doc != null) {
                LocalDate fechaVencimiento = LocalDate.parse(doc.getString("fechaVencimiento"));
                if (fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor)) {
                    Membresia m = new Membresia(
                        0, // id not used
                        usuarioId,
                        doc.getString("tipo"),
                        LocalDate.parse(doc.getString("fechaInicio")),
                        fechaVencimiento,
                        100.0, // precio not stored
                        doc.getString("estado"),
                        "" // fechaRegistro not stored
                    );
                    membresiasActivas.add(m);
                }
            }
            return membresiasActivas;
        } else {
            List<Membresia> membresiasActivas = new ArrayList<>();
            LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();

            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM membresias WHERE usuario_id=?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, usuarioId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            try {
                                LocalDate fechaVencimiento = LocalDate.parse(rs.getString("fecha_vencimiento"));
                                // Solo agregar si la membresía no está vencida
                                if (fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor)) {
                                    Membresia m = new Membresia(
                                        rs.getInt("id"),
                                        rs.getInt("usuario_id"),
                                        rs.getString("tipo_membresia"),
                                        LocalDate.parse(rs.getString("fecha_inicio")),
                                        fechaVencimiento,
                                        rs.getDouble("precio"),
                                        rs.getString("estado"),
                                        rs.getString("fecha_registro")
                                    );
                                    membresiasActivas.add(m);
                                }
                            } catch (Exception dateEx) {
                                logger.log(Level.SEVERE, "Error al parsear fechas: " + dateEx.getMessage());
                            }
                        }
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error al obtener membresías activas", e);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al conectar a base de datos", e);
            }
            return membresiasActivas;
        }
    }

    /**
     * Obtiene todas las membresías activas de un usuario por documento (para MongoDB y SQLite)
     * @param documento Número de documento del usuario
     * @return Lista de membresías activas
     */
    public List<Membresia> obtenerMembresiasActivasPorDocumento(String documento) {
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            List<Membresia> membresiasActivas = new ArrayList<>();
            LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
            Document doc = MongoDBService.obtenerMembresiaPorDocumento(documento);
            if (doc != null) {
                try {
                    LocalDate fechaVencimiento = LocalDate.parse(doc.getString("fechaVencimiento"));
                    if (fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor)) {
                        Membresia m = new Membresia(
                            0, // id no se usa en Mongo
                            0, // usuarioId no se usa en Mongo
                            doc.getString("tipo"),
                            LocalDate.parse(doc.getString("fechaInicio")),
                            fechaVencimiento,
                            100.0, // precio no almacenado
                            doc.getString("estado"),
                            "" // fechaRegistro no almacenado
                        );
                        membresiasActivas.add(m);
                    }
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error al parsear membresía de MongoDB: " + e.getMessage());
                }
            }
            return membresiasActivas;
        } else {
            // Para DBs relacionales (SQLite, MySQL), buscar por documento
            List<Membresia> membresiasActivas = new ArrayList<>();
            LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT m.* FROM membresias m INNER JOIN usuarios u ON m.usuario_id = u.id WHERE u.documento = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, documento);
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            try {
                                LocalDate fechaVencimiento = LocalDate.parse(rs.getString("fecha_vencimiento"));
                                if (fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor)) {
                                    Membresia m = new Membresia(
                                        rs.getInt("id"),
                                        rs.getInt("usuario_id"),
                                        rs.getString("tipo_membresia"),
                                        LocalDate.parse(rs.getString("fecha_inicio")),
                                        fechaVencimiento,
                                        rs.getDouble("precio"),
                                        rs.getString("estado"),
                                        rs.getString("fecha_registro")
                                    );
                                    membresiasActivas.add(m);
                                }
                            } catch (Exception dateEx) {
                                logger.log(Level.SEVERE, "Error al parsear fechas de membresía: " + dateEx.getMessage());
                            }
                        }
                    }
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error al obtener membresías por documento", e);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al conectar a base de datos", e);
            }
            return membresiasActivas;
        }
    }

    /**
     * Obtiene información de la sincronización con el servidor
     * @return String con la zona horaria del servidor
     */
    public String obtenerInfoServidor() {
        try {
            LocalDateTime horaServidor = TimeService.obtenerHoraDelServidor();
            String zonaHoraria = TimeService.obtenerZonaHoraria();
            return "Hora del servidor: " + horaServidor + " (" + zonaHoraria + ")";
        } catch (Exception e) {
            return "Error al conectar con el servidor de tiempo";
        }
    }

    /**
     * Renueva todas las membresías para que estén activas (30 días desde hoy)
     */
    public void renovarTodasLasMembresias() {
        LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
        LocalDate nuevaFechaVencimiento = hoyServidor.plusDays(30);
        if (DatabaseConnection.getEngine() == DatabaseConnection.DatabaseEngine.MONGODB) {
            try {
                MongoCollection<Document> collection = MongoDBService.getDatabase().getCollection("membresias");
                Document actualizacion = new Document("$set", new Document("fechaVencimiento", nuevaFechaVencimiento.toString()).append("estado", "activa"));
                collection.updateMany(new Document(), actualizacion);
                System.out.println("✓ Todas las membresías en MongoDB renovadas hasta " + nuevaFechaVencimiento);
            } catch (Exception e) {
                System.err.println("✗ Error al renovar membresías en MongoDB: " + e.getMessage());
            }
        } else {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "UPDATE membresias SET fecha_vencimiento = ?, estado = 'activa'";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setDate(1, Date.valueOf(nuevaFechaVencimiento));
                    int filasActualizadas = stmt.executeUpdate();
                    System.out.println("✓ " + filasActualizadas + " membresías renovadas hasta " + nuevaFechaVencimiento);
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al renovar membresías", e);
            }
        }
    }
}
