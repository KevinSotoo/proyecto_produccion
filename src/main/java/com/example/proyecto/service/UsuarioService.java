package com.example.proyecto.service;

import com.example.proyecto.model.Usuario;
import com.example.proyecto.util.DatabaseConnection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioService {

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.dir") + "/data/gimnasio_usuarios.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();
    private boolean usarBD = true; // Bandera para usar BD

    public void guardar(List<Usuario> usuarios) throws IOException {
        if (usarBD) {
            guardarEnBD(usuarios);
        } else {
            ARCHIVO_DATOS.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(ARCHIVO_DATOS, usuarios);
        }
    }

    public List<Usuario> cargar() throws IOException {
        if (usarBD) {
            return cargarDeBD();
        } else {
            if (!ARCHIVO_DATOS.exists()) return new ArrayList<>();
            return mapper.readValue(ARCHIVO_DATOS, new TypeReference<List<Usuario>>() {});
        }
    }

    // Métodos para trabajar con BD
    private void guardarEnBD(List<Usuario> usuarios) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            for (Usuario u : usuarios) {
                if (u.getId() == 0) {
                    // Insertar nuevo
                    String sql = "INSERT INTO usuarios (documento, nombre, edad, sexo, peso, altura, objetivo, calorias, tipo_membresia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, u.getDocumento());
                        stmt.setString(2, u.getNombre());
                        stmt.setInt(3, u.getEdad());
                        stmt.setString(4, u.getSexo());
                        stmt.setDouble(5, u.getPeso());
                        stmt.setDouble(6, u.getAltura());
                        stmt.setString(7, u.getObjetivo());
                        stmt.setDouble(8, u.getCalorias());
                        stmt.setString(9, u.getTipoMembresia());
                        stmt.executeUpdate();
                    }
                } else {
                    // Actualizar existente
                    String sql = "UPDATE usuarios SET nombre = ?, edad = ?, sexo = ?, peso = ?, altura = ?, objetivo = ?, calorias = ?, tipo_membresia = ? WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, u.getNombre());
                        stmt.setInt(2, u.getEdad());
                        stmt.setString(3, u.getSexo());
                        stmt.setDouble(4, u.getPeso());
                        stmt.setDouble(5, u.getAltura());
                        stmt.setString(6, u.getObjetivo());
                        stmt.setDouble(7, u.getCalorias());
                        stmt.setString(8, u.getTipoMembresia());
                        stmt.setInt(9, u.getId());
                        stmt.executeUpdate();
                    }
                }
            }
            System.out.println("✓ Datos guardados en BD correctamente");
        } catch (SQLException e) {
            System.out.println("✗ Error al guardar en BD: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Usuario> cargarDeBD() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, documento, nombre, edad, sexo, peso, altura, objetivo, calorias, tipo_membresia FROM usuarios";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Usuario u = new Usuario(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getInt("edad"),
                            rs.getDouble("peso"),
                            rs.getDouble("altura"),
                            rs.getString("objetivo"),
                            rs.getDouble("calorias"),
                            rs.getString("sexo"),
                            rs.getString("documento"),
                            false, // abandonado - se actualiza desde tabla abandonos
                            rs.getString("tipo_membresia")
                    );
                    usuarios.add(u);
                }
            }
            System.out.println("✓ Datos cargados desde BD: " + usuarios.size() + " usuarios");
        } catch (SQLException e) {
            System.out.println("✗ Error al cargar desde BD: " + e.getMessage());
            System.out.println("⚠ Intentando cargar desde JSON...");
            try {
                if (ARCHIVO_DATOS.exists()) {
                    usuarios = mapper.readValue(ARCHIVO_DATOS, new TypeReference<List<Usuario>>() {});
                    usarBD = false; // Cambiar a JSON si BD no está disponible
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return usuarios;
    }

    public void eliminarUsuario(int usuarioId) {
        if (usarBD) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM usuarios WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, usuarioId);
                    stmt.executeUpdate();
                    System.out.println("✓ Usuario eliminado de BD");
                }
            } catch (SQLException e) {
                System.out.println("✗ Error al eliminar usuario: " + e.getMessage());
            }
        }
    }

    // ...métodos de cálculo existentes...
    public double calcularCalorias(double peso, double altura, int edad, String sexo, String actividad, String objetivo) {
        double tmb;
        if (sexo.equalsIgnoreCase("Masculino")) {
            tmb = (10 * peso) + (6.25 * altura) - (5 * edad) + 5;
        } else {
            tmb = (10 * peso) + (6.25 * altura) - (5 * edad) - 161;
        }

        double factor;
        switch (actividad) {
            case "Ligero":      factor = 1.375; break;
            case "Moderado":    factor = 1.55;  break;
            case "Intenso":     factor = 1.725; break;
            case "Muy intenso": factor = 1.9;   break;
            default:            factor = 1.2;
        }

        double calorias = tmb * factor;

        if (objetivo.equalsIgnoreCase("Perder grasa")) {
            calorias -= 400;
        } else if (objetivo.equalsIgnoreCase("Ganar masa muscular")) {
            calorias += 400;
        }

        return Math.round(calorias);
    }

    public Map<String, Long> contarPorObjetivo(List<Usuario> usuarios) {
        return usuarios.stream()
                .collect(Collectors.groupingBy(Usuario::getObjetivo, Collectors.counting()));
    }

    public Map<String, List<Usuario>> agruparPorSexo(List<Usuario> usuarios) {
        return usuarios.stream()
                .filter(u -> u.getSexo() != null)
                .collect(Collectors.groupingBy(Usuario::getSexo));
    }

    /**
     * Obtiene el ID de un usuario por su nombre
     */
    public int obtenerIdPorNombre(String nombre) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM usuarios WHERE nombre = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener ID del usuario: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Guarda un único usuario
     */
    public void guardar(Usuario usuario) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuarios (documento, nombre, edad, sexo, peso, altura, objetivo, calorias, tipo_membresia) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, usuario.getDocumento());
                stmt.setString(2, usuario.getNombre());
                stmt.setInt(3, usuario.getEdad());
                stmt.setString(4, usuario.getSexo());
                stmt.setDouble(5, usuario.getPeso());
                stmt.setDouble(6, usuario.getAltura());
                stmt.setString(7, usuario.getObjetivo());
                stmt.setDouble(8, usuario.getCalorias());
                stmt.setString(9, usuario.getTipoMembresia());
                stmt.executeUpdate();
                System.out.println("✓ Usuario " + usuario.getNombre() + " guardado correctamente");
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al guardar usuario: " + e.getMessage());
        }
    }
}