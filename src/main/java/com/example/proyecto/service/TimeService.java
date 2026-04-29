package com.example.proyecto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Servicio para consumir World Time API
 * Obtiene la fecha y hora actual del servidor para validaciones
 */
public class TimeService {
    private static final String API_URL = "http://worldtimeapi.org/api/timezone/America/Bogota";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Obtiene la fecha actual desde el servidor World Time API
     * @return LocalDate con la fecha del servidor
     */
    public static LocalDate obtenerFechaDelServidor() {
        try {
            String jsonResponse = consumirAPI();
            JsonNode node = objectMapper.readTree(jsonResponse);
            
            // Obtener la fecha desde utc_datetime
            String utcDateTime = node.get("utc_datetime").asText();
            // Formato: 2024-04-29T15:30:45.123456Z
            LocalDateTime dateTime = LocalDateTime.parse(utcDateTime, 
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            return dateTime.toLocalDate();
        } catch (Exception e) {
            System.err.println("Error al obtener fecha del servidor: " + e.getMessage());
            // Si falla, retorna la fecha local
            return LocalDate.now();
        }
    }

    /**
     * Obtiene la hora actual desde el servidor World Time API
     * @return LocalDateTime con la fecha y hora del servidor
     */
    public static LocalDateTime obtenerHoraDelServidor() {
        try {
            String jsonResponse = consumirAPI();
            JsonNode node = objectMapper.readTree(jsonResponse);
            
            String utcDateTime = node.get("utc_datetime").asText();
            return LocalDateTime.parse(utcDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            System.err.println("Error al obtener hora del servidor: " + e.getMessage());
            return LocalDateTime.now();
        }
    }

    /**
     * Obtiene la zona horaria actual
     * @return String con la zona horaria (ej: "America/Bogota")
     */
    public static String obtenerZonaHoraria() {
        try {
            String jsonResponse = consumirAPI();
            JsonNode node = objectMapper.readTree(jsonResponse);
            return node.get("timezone").asText();
        } catch (Exception e) {
            System.err.println("Error al obtener zona horaria: " + e.getMessage());
            return "America/Bogota";
        }
    }

    /**
     * Método privado para consumir la API
     * @return String con la respuesta JSON
     */
    private static String consumirAPI() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("Error en la API: código " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return response.toString();
    }
}

