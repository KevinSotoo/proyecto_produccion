package com.example.proyecto;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;


public class GestionUsuariosController {

    @FXML
    private TableView<Usuario> usuariosTable;

    @FXML private ComboBox<String> actividadBox;

    @FXML
    private TextField edadField;

    @FXML
    private TableColumn<Usuario, String> nombreColumn;

    @FXML
    private TableColumn<Usuario, Integer> edadColumn;

    @FXML
    private TableColumn<Usuario, Double> pesoColumn;

    @FXML
    private TableColumn<Usuario, Double> alturaColumn;

    @FXML
    private TableColumn<Usuario, String> objetivoColumn;

    @FXML
    private TableColumn<Usuario, Double> caloriasColumn;

    @FXML
    private ComboBox<String> sexoComboBox;

    @FXML
    private ComboBox<String> objetivoComboBox;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField pesoField;

    @FXML
    private TextField alturaField;

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.home") + "/gimnasio_usuarios.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    private void guardarDatos() {
        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(ARCHIVO_DATOS, usuariosTable.getItems());
        } catch (IOException e) {
            mostrarAlerta("Error al guardar datos: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        if (!ARCHIVO_DATOS.exists()) return;
        try {
            List<Usuario> usuarios = mapper.readValue(
                    ARCHIVO_DATOS,
                    new TypeReference<List<Usuario>>() {}
            );
            usuariosTable.getItems().addAll(usuarios);
        } catch (IOException e) {
            mostrarAlerta("Error al cargar datos: " + e.getMessage());
        }
    }
    @FXML
    private void irEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/Estadisticas.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usuariosTable.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error al abrir estadísticas: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        edadColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));
        pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
        alturaColumn.setCellValueFactory(new PropertyValueFactory<>("altura"));
        objetivoColumn.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
        caloriasColumn.setCellValueFactory(new PropertyValueFactory<>("calorias"));


        actividadBox.getItems().addAll(
                "Sedentario",
                "Ligero",
                "Moderado",
                "Intenso",
                "Muy intenso"
        );
        sexoComboBox.getItems().addAll(
                "Masculino",
                "Femenino"
        );

        objetivoComboBox.getItems().addAll(
                "Perder grasa",
                "Ganar masa muscular",
                "Mantener peso"
        );
        cargarDatos();
    }
    @FXML
    private void calcularRequerimientos() {

        try {

            String nombre = nombreField.getText();
            int edad = Integer.parseInt(edadField.getText());
            double peso = Double.parseDouble(pesoField.getText());
            double altura = Double.parseDouble(alturaField.getText()); // en CM
            String actividad = actividadBox.getValue();
            String objetivo = objetivoComboBox.getValue();
            String sexo = sexoComboBox.getValue();

            // 🔹 1. Calcular TMB
            double tmb;

            if (sexo.equalsIgnoreCase("Hombre")) {
                tmb = (10 * peso) + (6.25 * altura) - (5 * edad) + 5;
            } else {
                tmb = (10 * peso) + (6.25 * altura) - (5 * edad) - 161;
            }

            // 🔹 2. Factor de actividad
            double factor = 1.2;

            switch (actividad) {
                case "Sedentario":
                    factor = 1.2;
                    break;
                case "Ligero":
                    factor = 1.375;
                    break;
                case "Moderado":
                    factor = 1.55;
                    break;
                case "Intenso":
                    factor = 1.725;
                    break;
                case "Muy intenso":
                    factor = 1.9;
                    break;
            }

            double calorias = tmb * factor;

            // 🔹 3. Ajustar según objetivo
            if (objetivo.equalsIgnoreCase("Perder grasa")) {
                calorias = calorias - 400;
            } else if (objetivo.equalsIgnoreCase("Ganar masa")) {
                calorias = calorias + 400;
            }

            calorias = Math.round(calorias);

            // 🔹 4. Crear usuario y agregar a tabla
            Usuario usuario = new Usuario(nombre, edad, peso, altura, objetivo, calorias);
            usuariosTable.getItems().add(usuario);
            guardarDatos();

            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Verifica que todos los campos estén llenos correctamente.");
        }
    }
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private void limpiarCampos() {
        nombreField.clear();
        edadField.clear();
        pesoField.clear();
        alturaField.clear();
        actividadBox.setValue(null);
    }
}
