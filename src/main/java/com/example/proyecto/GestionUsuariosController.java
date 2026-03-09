package com.example.proyecto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionUsuariosController {

    @FXML private TableView<Usuario> usuariosTable;
    @FXML private ComboBox<String> actividadBox;
    @FXML private TextField edadField;
    @FXML private TableColumn<Usuario, String> nombreColumn;
    @FXML private TableColumn<Usuario, Integer> edadColumn;
    @FXML private TableColumn<Usuario, Double> pesoColumn;
    @FXML private TableColumn<Usuario, Double> alturaColumn;
    @FXML private TableColumn<Usuario, String> objetivoColumn;
    @FXML private TableColumn<Usuario, Double> caloriasColumn;
    @FXML private TableColumn<Usuario, String> sexoColumn;
    @FXML private ComboBox<String> sexoComboBox;
    @FXML private ComboBox<String> objetivoComboBox;
    @FXML private TextField nombreField;
    @FXML private TextField pesoField;
    @FXML private TextField alturaField;

    private static final File ARCHIVO_DATOS = new File(
            System.getProperty("user.dir") + "/data/gimnasio_usuarios.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        edadColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));
        pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
        alturaColumn.setCellValueFactory(new PropertyValueFactory<>("altura"));
        objetivoColumn.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
        caloriasColumn.setCellValueFactory(new PropertyValueFactory<>("calorias"));
        sexoColumn.setCellValueFactory(new PropertyValueFactory<>("sexo"));

        actividadBox.getItems().addAll(
                "Sedentario", "Ligero", "Moderado", "Intenso", "Muy intenso"
        );
        sexoComboBox.getItems().addAll("Masculino", "Femenino");
        objetivoComboBox.getItems().addAll(
                "Perder grasa", "Ganar masa muscular", "Mantener peso"
        );

        // Al seleccionar fila, cargar datos en el formulario
        usuariosTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> { if (newVal != null) cargarEnFormulario(newVal); }
        );

        cargarDatos();
    }

    private void cargarEnFormulario(Usuario u) {
        nombreField.setText(u.getNombre());
        edadField.setText(String.valueOf(u.getEdad()));
        pesoField.setText(String.valueOf(u.getPeso()));
        alturaField.setText(String.valueOf(u.getAltura()));
        sexoComboBox.setValue(u.getSexo());
        objetivoComboBox.setValue(u.getObjetivo());
    }

    @FXML
    private void calcularRequerimientos() {
        try {
            String nombre = nombreField.getText();
            int edad = Integer.parseInt(edadField.getText());
            double peso = Double.parseDouble(pesoField.getText());
            double altura = Double.parseDouble(alturaField.getText());
            String actividad = actividadBox.getValue();
            String objetivo = objetivoComboBox.getValue();
            String sexo = sexoComboBox.getValue();

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

            calorias = Math.round(calorias);

            Usuario usuario = new Usuario(nombre, edad, peso, altura, objetivo, calorias, sexo);
            usuariosTable.getItems().add(usuario);
            guardarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Verifica que todos los campos estén llenos correctamente.");
        }
    }

    @FXML
    private void actualizarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un usuario de la tabla para actualizar.");
            return;
        }

        try {
            seleccionado.setNombre(nombreField.getText());
            seleccionado.setEdad(Integer.parseInt(edadField.getText()));
            seleccionado.setPeso(Double.parseDouble(pesoField.getText()));
            seleccionado.setAltura(Double.parseDouble(alturaField.getText()));
            seleccionado.setObjetivo(objetivoComboBox.getValue());
            seleccionado.setSexo(sexoComboBox.getValue());

            usuariosTable.refresh();
            guardarDatos();
            limpiarCampos();

        } catch (Exception e) {
            mostrarAlerta("Verifica que todos los campos estén llenos correctamente.");
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un usuario de la tabla para eliminar.");
            return;
        }
        usuariosTable.getItems().remove(seleccionado);
        guardarDatos();
    }

    @FXML
    private void irEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/Estadisticas.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usuariosTable.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            mostrarAlerta("Error al abrir estadísticas: " + e.getMessage());
        }
    }

    @FXML
    private void salir() {
        Stage stage = (Stage) usuariosTable.getScene().getWindow();
        stage.close();
    }

    private void guardarDatos() {
        try {
            ARCHIVO_DATOS.getParentFile().mkdirs();
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
        sexoComboBox.setValue(null);
        objetivoComboBox.setValue(null);
    }
}