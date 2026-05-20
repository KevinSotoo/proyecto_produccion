package com.example.proyecto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Servicio para consumir APIs de tiempo con múltiples fuentes
 * Intenta varias APIs hasta obtener la fecha del servidor
 *
 * Prioridad de APIs:
 * 1. https://worldtimeapi.org/api/timezone/America/Bogota (user preference)
 * 2. http://worldtimeapi.org/api/timezone/America/Bogota (HTTP fallback)
 * 3. http://timeapi.io/api/timezone/America/Bogota (API alternativa)
 * 4. https://timeapi.io/api/timezone/America/Bogota (HTTPS alternativa)
 */
public class TimeService {

    // APIs en orden de preferencia (priorizamos timeapi.io tal como solicitaste)
    private static final String[] APIS = {
        "https://timeapi.io/api/Time/current/zone?timeZone=America/Bogota", // timeapi.io (HTTPS)
        "http://timeapi.io/api/Time/current/zone?timeZone=America/Bogota",  // timeapi.io (HTTP fallback)
        "https://worldtimeapi.org/api/timezone/America/Bogota",            // worldtimeapi (fallback)
        "http://worldtimeapi.org/api/timezone/America/Bogota"              // worldtimeapi HTTP
    };

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int REINTENTOS = 2;
    private static final int TIMEOUT_MS = 5000;

    /**
     * Obtiene la fecha actual desde una API de tiempo (intenta múltiples fuentes)
     */
    public static LocalDate obtenerFechaDelServidor() {
        System.out.println("\n[TimeService] Obteniendo fecha del servidor...");

        for (int i = 0; i < APIS.length; i++) {
            String apiUrl = APIS[i];
            try {
                System.out.println("  → Intento " + (i + 1) + "/" + APIS.length + ": " + apiUrl);
                String jsonResponse = consumirAPIConReintentos(apiUrl, REINTENTOS);
                LocalDate fecha = parsearFecha(jsonResponse);
                System.out.println("  ✓ ÉXITO - Fecha obtenida: " + fecha);
                return fecha;
            } catch (Exception e) {
                System.out.println("  ✗ Falló (" + e.getClass().getSimpleName() + "): " + e.getMessage());
            }
        }

        // Si todas las APIs fallan, usar fecha local (fallback final)
        System.out.println("  ⚠ Todas las APIs fallaron. Usando fecha LOCAL: " + LocalDate.now());
        return LocalDate.now();
    }

    /**
     * Obtiene la hora actual desde una API de tiempo
     */
    public static LocalDateTime obtenerHoraDelServidor() {
        System.out.println("\n[TimeService] Obteniendo hora del servidor...");

        for (int i = 0; i < APIS.length; i++) {
            String apiUrl = APIS[i];
            try {
                System.out.println("  → Intento " + (i + 1) + "/" + APIS.length + ": " + apiUrl);
                String jsonResponse = consumirAPIConReintentos(apiUrl, REINTENTOS);
                LocalDateTime hora = parsearHora(jsonResponse);
                System.out.println("  ✓ ÉXITO - Hora obtenida: " + hora);
                return hora;
            } catch (Exception e) {
                System.out.println("  ✗ Falló: " + e.getMessage());
            }
        }

        System.out.println("  ⚠ Usando hora LOCAL: " + LocalDateTime.now());
        return LocalDateTime.now();
    }

    /**
     * Obtiene la zona horaria
     */
    public static String obtenerZonaHoraria() {
        try {
            String jsonResponse = consumirAPIConReintentos(APIS[0], REINTENTOS);
            JsonNode node = objectMapper.readTree(jsonResponse);
            return node.get("timezone").asText("America/Bogota");
        } catch (Exception e) {
            return "America/Bogota";
        }
    }

    /**
     * Intenta consumir una API con reintentos
     */
    private static String consumirAPIConReintentos(String apiUrl, int reintentos) throws Exception {
        Exception ultimaExcepcion = null;

        for (int intento = 1; intento <= reintentos; intento++) {
            try {
                System.out.println("      [Intento " + intento + "/" + reintentos + "]");
                return consumirAPIconURL(apiUrl);
            } catch (Exception e) {
                ultimaExcepcion = e;
                if (intento < reintentos) {
                    System.out.println("      → Reintentando en 1 segundo...");
                    try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }

        throw ultimaExcepcion != null ? ultimaExcepcion : new Exception("Fallos en todos los intentos");
    }

    /**
     * Realiza la conexión HTTP a una URL
     */
    private static String consumirAPIconURL(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new Exception("HTTP " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Parsea la fecha desde la respuesta JSON de cualquier API
     */
    private static LocalDate parsearFecha(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);

        // Intentar obtener utc_datetime (World Time API)
        if (node.has("utc_datetime")) {
            String utcDateTime = node.get("utc_datetime").asText();
            OffsetDateTime odt = OffsetDateTime.parse(utcDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return odt.toLocalDate();
        }

        // Intentar datetime (timeapi.io) - soportar varios formatos comunes
        if (node.has("datetime") || node.has("dateTime") || node.has("date_time")) {
            String datetime = node.has("datetime") ? node.get("datetime").asText() : node.has("dateTime") ? node.get("dateTime").asText() : node.get("date_time").asText();
            // Algunos proveedores devuelven offset, otros devuelven local datetime sin offset
            try {
                OffsetDateTime odt = OffsetDateTime.parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return odt.toLocalDate();
            } catch (Exception ex) {
                // Intentar parsear solo la fecha si no tiene offset
                try {
                    return LocalDate.parse(datetime.substring(0, 10));
                } catch (Exception ex2) {
                    throw new Exception("No se pudo parsear datetime: " + datetime);
                }
            }
        }

        throw new Exception("No se encontró campo de fecha en JSON");
    }

    /**
     * Parsea la hora desde la respuesta JSON
     */
    private static LocalDateTime parsearHora(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);

        if (node.has("utc_datetime")) {
            String utcDateTime = node.get("utc_datetime").asText();
            OffsetDateTime odt = OffsetDateTime.parse(utcDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return odt.toLocalDateTime();
        }

        if (node.has("datetime") || node.has("dateTime") || node.has("date_time")) {
            String datetime = node.has("datetime") ? node.get("datetime").asText() : node.has("dateTime") ? node.get("dateTime").asText() : node.get("date_time").asText();
            try {
                OffsetDateTime odt = OffsetDateTime.parse(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                return odt.toLocalDateTime();
            } catch (Exception ex) {
                // Si viene sin offset, intentar parsear como LocalDateTime ISO
                try {
                    return LocalDateTime.parse(datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                } catch (Exception ex2) {
                    throw new Exception("No se pudo parsear datetime/hora: " + datetime);
                }
            }
        }

        throw new Exception("No se encontró campo de hora en JSON");
    }
}

