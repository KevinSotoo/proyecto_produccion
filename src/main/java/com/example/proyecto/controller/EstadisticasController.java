package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EstadisticasController {

    @FXML private PieChart pieChart;
    @FXML private Label totalUsuariosLabel;
    @FXML private Label perdidaLabel;
    @FXML private Label gananciaLabel;
    @FXML private Label mantenerLabel;
    @FXML private Label edadMasculinoLabel;
    @FXML private Label pesoMasculinoLabel;
    @FXML private Label alturaMasculinoLabel;
    @FXML private Label edadFemeninoLabel;
    @FXML private Label pesoFemeninoLabel;
    @FXML private Label alturaFemeninoLabel;

    private final UsuarioService service = new UsuarioService();

    @FXML
    public void initialize() {
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        try {
            List<Usuario> usuarios = service.cargar();
            if (usuarios.isEmpty()) return;

            // ── Pastel por objetivo ──
            Map<String, Long> conteo = service.contarPorObjetivo(usuarios);

            var datos = FXCollections.observableArrayList(
                    conteo.entrySet().stream()
                            .map(e -> new PieChart.Data(e.getKey() + " (" + e.getValue() + ")", e.getValue()))
                            .toList()
            );
            pieChart.setData(datos);
            pieChart.setTitle("Distribución por Objetivo");
            pieChart.setLabelsVisible(true);

            totalUsuariosLabel.setText("Total usuarios: " + usuarios.size());
            perdidaLabel.setText("Perder grasa: " + conteo.getOrDefault("Perder grasa", 0L));
            gananciaLabel.setText("Ganar masa muscular: " + conteo.getOrDefault("Ganar masa muscular", 0L));
            mantenerLabel.setText("Mantener peso: " + conteo.getOrDefault("Mantener peso", 0L));

            // ── Promedios por sexo ──
            Map<String, List<Usuario>> porSexo = service.agruparPorSexo(usuarios);

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
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            System.err.println("Error al volver: " + e.getMessage());
        }
    }
}