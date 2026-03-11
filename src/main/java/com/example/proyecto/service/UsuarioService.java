package com.example.proyecto.service;

import com.example.proyecto.model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioService {

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.dir") + "/data/gimnasio_usuarios.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    public void guardar(List<Usuario> usuarios) throws IOException {
        ARCHIVO_DATOS.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(ARCHIVO_DATOS, usuarios);
    }

    public List<Usuario> cargar() throws IOException {
        if (!ARCHIVO_DATOS.exists()) return new ArrayList<>();
        return mapper.readValue(ARCHIVO_DATOS, new TypeReference<List<Usuario>>() {});
    }

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
}