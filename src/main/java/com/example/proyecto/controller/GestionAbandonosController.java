package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.Abandono;
import com.example.proyecto.service.AbandonoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class GestionAbandonosController {

    @FXML
    private TextField txtNombre, txtMotivo;

    @FXML
    private TableView<Abandono> tablaAbandonos;

    @FXML
    private TableColumn<Abandono, Integer> colId;

    @FXML
    private TableColumn<Abandono, String> colNombre;

    @FXML
    private TableColumn<Abandono, LocalDate> colFecha;

    @FXML
    private TableColumn<Abandono, String> colMotivo;

    @FXML
    private Button btnAgregar, btnBuscar, btnEliminar, btnVolver;

    private ObservableList<Abandono> listaAbandonos = FXCollections.observableArrayList();
    private final AbandonoService abandonoService = new AbandonoService();

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(data -> data.getValue().nombreUsuarioProperty());
        colFecha.setCellValueFactory(data -> data.getValue().fechaAbandonoProperty());
        colMotivo.setCellValueFactory(data -> data.getValue().motivoProperty());

        tablaAbandonos.setItems(listaAbandonos);

        // Cargar datos de abandonos
        cargarAbandonos();

        // Eventos de botones
        btnAgregar.setOnAction(event -> agregarAbandono());
        btnEliminar.setOnAction(event -> eliminarAbandono());
        btnBuscar.setOnAction(event -> buscarAbandono());
    }

    private void cargarAbandonos() {
        try {
            listaAbandonos.clear();
            listaAbandonos.addAll(abandonoService.cargar());
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al cargar abandonos: " + e.getMessage());
        }
    }

    private void agregarAbandono() {
        String nombre = txtNombre.getText();
        String motivo = txtMotivo.getText();

        if (nombre.isEmpty() || motivo.isEmpty()) {
            mostrarAlerta("Error", "Por favor, complete todos los campos.");
            return;
        }

        try {
            abandonoService.agregarAbandono(nombre, motivo);
            cargarAbandonos();
            txtNombre.clear();
            txtMotivo.clear();
            mostrarAlerta("Éxito", "Abandono registrado correctamente.");
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al guardar abandono: " + e.getMessage());
        }
    }

    private void eliminarAbandono() {
        Abandono seleccionado = tablaAbandonos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Seleccione un abandono para eliminar.");
            return;
        }

        try {
            listaAbandonos.remove(seleccionado);
            abandonoService.guardar(listaAbandonos);
            mostrarAlerta("Éxito", "Abandono eliminado correctamente.");
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al eliminar abandono: " + e.getMessage());
        }
    }

    private void buscarAbandono() {
        String nombre = txtNombre.getText().toLowerCase();
        if (nombre.isEmpty()) {
            cargarAbandonos();
            return;
        }

        ObservableList<Abandono> resultados = FXCollections.observableArrayList();
        for (Abandono abandono : listaAbandonos) {
            if (abandono.getNombreUsuario().toLowerCase().contains(nombre)) {
                resultados.add(abandono);
            }
        }

        tablaAbandonos.setItems(resultados);
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/GestionUsuarios.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) tablaAbandonos.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al volver: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}