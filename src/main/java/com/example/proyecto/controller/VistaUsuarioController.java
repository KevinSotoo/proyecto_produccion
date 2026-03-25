package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class VistaUsuarioController {

    @FXML private TableView<Usuario> usuarioTable;
    @FXML private TableColumn<Usuario, String> documentoColumn;
    @FXML private TableColumn<Usuario, String> nombreColumn;
    @FXML private TableColumn<Usuario, Integer> edadColumn;
    @FXML private TableColumn<Usuario, Double> pesoColumn;
    @FXML private TableColumn<Usuario, Double> alturaColumn;
    @FXML private TableColumn<Usuario, String> objetivoColumn;
    @FXML private TableColumn<Usuario, Double> caloriasColumn;
    @FXML private TableColumn<Usuario, String> sexoColumn;
    @FXML private TextField pesoField;
    @FXML private TextField alturaField;
    @FXML private ComboBox<String> actividadBox;
    @FXML private ComboBox<String> objetivoComboBox;
    @FXML private Label bienvenidaLabel;

    private String documentoActual;
    private Usuario usuarioActual;
    private final UsuarioService service = new UsuarioService();

    @FXML
    public void initialize() {
        documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
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
        objetivoComboBox.getItems().addAll(
                "Perder grasa", "Ganar masa muscular", "Mantener peso"
        );
    }

    public void setDocumento(String documento) {
        this.documentoActual = documento;
        bienvenidaLabel.setText("Bienvenido: " + documento);
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        try {
            List<Usuario> todos = service.cargar();
            usuarioActual = todos.stream()
                    .filter(u -> documentoActual.equals(u.getDocumento()))
                    .findFirst()
                    .orElse(null);

            if (usuarioActual != null) {
                usuarioTable.getItems().clear();
                usuarioTable.getItems().add(usuarioActual);

                // Prellenar formulario con sus datos actuales
                pesoField.setText(String.valueOf(usuarioActual.getPeso()));
                alturaField.setText(String.valueOf(usuarioActual.getAltura()));
                objetivoComboBox.setValue(usuarioActual.getObjetivo());
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar tus datos: " + e.getMessage());
        }
    }

    @FXML
    private void guardarCambios() {
        if (usuarioActual == null) {
            mostrarAlerta("No se encontró tu perfil.");
            return;
        }
        try {
            double nuevoPeso = Double.parseDouble(pesoField.getText());
            double nuevaAltura = Double.parseDouble(alturaField.getText());
            String nuevaActividad = actividadBox.getValue();
            String nuevoObjetivo = objetivoComboBox.getValue();

            if (nuevoObjetivo == null) {
                mostrarAlerta("Selecciona un objetivo.");
                return;
            }

            // Recalcular calorías
            double nuevasCalorias = service.calcularCalorias(
                    nuevoPeso, nuevaAltura,
                    usuarioActual.getEdad(),
                    usuarioActual.getSexo(),
                    nuevaActividad != null ? nuevaActividad : "Sedentario",
                    nuevoObjetivo
            );

            // Actualizar el usuario
            usuarioActual.setPeso(nuevoPeso);
            usuarioActual.setAltura(nuevaAltura);
            usuarioActual.setObjetivo(nuevoObjetivo);
            usuarioActual.setCalorias(nuevasCalorias);

            // Guardar todos los usuarios con el actualizado
            List<Usuario> todos = service.cargar();
            for (int i = 0; i < todos.size(); i++) {
                if (documentoActual.equals(todos.get(i).getDocumento())) {
                    todos.set(i, usuarioActual);
                    break;
                }
            }
            service.guardar(todos);

            // Refrescar tabla
            usuarioTable.getItems().clear();
            usuarioTable.getItems().add(usuarioActual);

            mostrarExito("Datos actualizados correctamente.");

        } catch (Exception e) {
            mostrarAlerta("Verifica que los campos estén llenos correctamente.");
        }
    }

    @FXML
    private void salir() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/Login.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usuarioTable.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}