package com.example.proyecto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EstadisticasController {

    @FXML private PieChart pieChart;
    @FXML private Label totalUsuariosLabel;
    @FXML private Label perdidaLabel;
    @FXML private Label gananciaLabel;
    @FXML private Label mantenerLabel;

    // Labels promedios por sexo
    @FXML private Label edadMasculinoLabel;
    @FXML private Label pesoMasculinoLabel;
    @FXML private Label alturaMasculinoLabel;
    @FXML private Label edadFemeninoLabel;
    @FXML private Label pesoFemeninoLabel;
    @FXML private Label alturaFemeninoLabel;

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.dir") + "/data/gimnasio_usuarios.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        if (!ARCHIVO_DATOS.exists()) return;

        try {
            List<Usuario> usuarios = mapper.readValue(
                    ARCHIVO_DATOS,
                    new TypeReference<List<Usuario>>() {}
            );

            if (usuarios.isEmpty()) return;

            // ── Pastel por objetivo ──
            Map<String, Long> conteo = usuarios.stream()
                    .collect(Collectors.groupingBy(Usuario::getObjetivo, Collectors.counting()));

            var datos = FXCollections.observableArrayList(
                    conteo.entrySet().stream()
                            .map(e -> new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()))
                            .collect(Collectors.toList())
            );
            pieChart.setData(datos);
            pieChart.setTitle("Distribución por Objetivo");
            pieChart.setLabelsVisible(true);

            totalUsuariosLabel.setText("Total usuarios: " + usuarios.size());
            perdidaLabel.setText("Perder grasa: " + conteo.getOrDefault("Perder grasa", 0L));
            gananciaLabel.setText("Ganar masa muscular: " + conteo.getOrDefault("Ganar masa muscular", 0L));
            mantenerLabel.setText("Mantener peso: " + conteo.getOrDefault("Mantener peso", 0L));

            // ── Promedios por sexo ──
            Map<String, List<Usuario>> porSexo = usuarios.stream()
                    .filter(u -> u.getSexo() != null)
                    .collect(Collectors.groupingBy(Usuario::getSexo));

            List<Usuario> masculinos = porSexo.getOrDefault("Masculino", List.of());
            List<Usuario> femeninos  = porSexo.getOrDefault("Femenino",  List.of());

            if (!masculinos.isEmpty()) {
                edadMasculinoLabel.setText(String.format("Edad: %.1f años",
                        masculinos.stream().mapToInt(Usuario::getEdad).average().orElse(0)));
                pesoMasculinoLabel.setText(String.format("Peso: %.1f kg",
                        masculinos.stream().mapToDouble(Usuario::getPeso).average().orElse(0)));
                alturaMasculinoLabel.setText(String.format("Altura: %.1f cm",
                        masculinos.stream().mapToDouble(Usuario::getAltura).average().orElse(0)));
            }

            if (!femeninos.isEmpty()) {
                edadFemeninoLabel.setText(String.format("Edad: %.1f años",
                        femeninos.stream().mapToInt(Usuario::getEdad).average().orElse(0)));
                pesoFemeninoLabel.setText(String.format("Peso: %.1f kg",
                        femeninos.stream().mapToDouble(Usuario::getPeso).average().orElse(0)));
                alturaFemeninoLabel.setText(String.format("Altura: %.1f cm",
                        femeninos.stream().mapToDouble(Usuario::getAltura).average().orElse(0)));
            }

        } catch (IOException e) {
            System.err.println("Error al cargar estadísticas: " + e.getMessage());
        }
    }

    @FXML
    private void volverGestion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/GestionUsuarios.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) pieChart.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            System.err.println("Error al volver: " + e.getMessage());
        }
    }
}