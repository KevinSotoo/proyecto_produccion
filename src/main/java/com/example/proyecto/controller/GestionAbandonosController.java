package com.example.proyecto.controller;

import com.example.proyecto.model.Abandono;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

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

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNombre.setCellValueFactory(data -> data.getValue().nombreUsuarioProperty());
        colFecha.setCellValueFactory(data -> data.getValue().fechaAbandonoProperty());
        colMotivo.setCellValueFactory(data -> data.getValue().motivoProperty());

        tablaAbandonos.setItems(listaAbandonos);

        // Eventos de botones
        btnAgregar.setOnAction(event -> agregarAbandono());
        btnEliminar.setOnAction(event -> eliminarAbandono());
        btnBuscar.setOnAction(event -> buscarAbandono());
        btnVolver.setOnAction(event -> volver());
    }

    private void agregarAbandono() {
        String nombre = txtNombre.getText();
        String motivo = txtMotivo.getText();

        if (nombre.isEmpty() || motivo.isEmpty()) {
            mostrarAlerta("Error", "Por favor, complete todos los campos.");
            return;
        }

        int id = listaAbandonos.size() + 1;
        Abandono abandono = new Abandono(id, nombre, LocalDate.now(), motivo);
        listaAbandonos.add(abandono);

        txtNombre.clear();
        txtMotivo.clear();
    }

    private void eliminarAbandono() {
        Abandono seleccionado = tablaAbandonos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error", "Seleccione un abandono para eliminar.");
            return;
        }

        listaAbandonos.remove(seleccionado);
    }

    private void buscarAbandono() {
        String nombre = txtNombre.getText().toLowerCase();
        ObservableList<Abandono> resultados = FXCollections.observableArrayList();

        for (Abandono abandono : listaAbandonos) {
            if (abandono.getNombreUsuario().toLowerCase().contains(nombre)) {
                resultados.add(abandono);
            }
        }

        tablaAbandonos.setItems(resultados);
    }

    private void volver() {
        // Lógica para volver a la ventana principal
        // Ejemplo: cargar otro FXML
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}