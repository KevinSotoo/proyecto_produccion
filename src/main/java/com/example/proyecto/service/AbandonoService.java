package com.example.proyecto.service;

import com.example.proyecto.model.Abandono;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbandonoService {

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.dir") + "/data/abandonos.json"
    );
    private final ObjectMapper mapper;

    public AbandonoService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void guardar(List<Abandono> abandonos) throws IOException {
        File parentDir = ARCHIVO_DATOS.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(ARCHIVO_DATOS, abandonos);
    }

    public List<Abandono> cargar() throws IOException {
        if (!ARCHIVO_DATOS.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(ARCHIVO_DATOS, new TypeReference<>() {});
        } catch (IOException e) {
            // Si hay error al deserializar, retornar lista vacía
            return new ArrayList<>();
        }
    }

    public void agregarAbandono(String nombreUsuario, String motivo) throws IOException {
        List<Abandono> abandonos = cargar();
        int nuevoId = abandonos.isEmpty() ? 1 : abandonos.stream()
                .mapToInt(Abandono::getId)
                .max()
                .orElse(0) + 1;

        Abandono nuevoAbandono = new Abandono(nuevoId, nombreUsuario, LocalDate.now(), motivo);
        abandonos.add(nuevoAbandono);
        guardar(abandonos);
    }
}




