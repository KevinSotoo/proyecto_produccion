package com.example.proyecto.service;

import com.example.proyecto.model.Membresia;
import com.example.proyecto.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MembresiaService {
    public List<Membresia> cargar() {
        List<Membresia> membresias = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM membresias";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Membresia m = new Membresia(
                        rs.getInt("id"),
                        rs.getInt("usuario_id"),
                        rs.getString("tipo_membresia"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_vencimiento").toLocalDate(),
                        rs.getDouble("precio"),
                        rs.getString("estado"),
                        rs.getString("fecha_registro")
                    );
                    membresias.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membresias;
    }

    public void guardar(Membresia m) {
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                        LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                        LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
                        return fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene todas las membresías activas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de membresías activas
     */
    public List<Membresia> obtenerMembresiasActivasDelUsuario(int usuarioId) {
        List<Membresia> membresiasActivas = new ArrayList<>();
        LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM membresias WHERE usuario_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, usuarioId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                        // Solo agregar si la membresía no está vencida
                        if (fechaVencimiento.isAfter(hoyServidor) || fechaVencimiento.isEqual(hoyServidor)) {
                            Membresia m = new Membresia(
                                rs.getInt("id"),
                                rs.getInt("usuario_id"),
                                rs.getString("tipo_membresia"),
                                rs.getDate("fecha_inicio").toLocalDate(),
                                fechaVencimiento,
                                rs.getDouble("precio"),
                                rs.getString("estado"),
                                rs.getString("fecha_registro")
                            );
                            membresiasActivas.add(m);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membresiasActivas;
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
}

