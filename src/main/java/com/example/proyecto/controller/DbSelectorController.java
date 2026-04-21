package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.util.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;

public class DbSelectorController {

    @FXML
    private RadioButton mysqlRadio;

    @FXML
    private RadioButton sqliteRadio;

    @FXML
    private ToggleGroup dbToggleGroup;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        mysqlRadio.setSelected(true);
    }

    @FXML
    private void continuarAlLogin() {
        DatabaseConnection.DatabaseEngine engine = sqliteRadio.isSelected()
                ? DatabaseConnection.DatabaseEngine.SQLITE
                : DatabaseConnection.DatabaseEngine.MYSQL;

        try {
            DatabaseConnection.setEngine(engine);
            DatabaseConnection.testConnection();
            abrirLogin();
        } catch (IllegalStateException e) {
            errorLabel.setText("No se pudo cargar el driver: " + e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("No se pudo inicializar la conexion: " + e.getMessage());
        }
    }

    private void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/proyecto/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) mysqlRadio.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            errorLabel.setText("Error al abrir login: " + e.getMessage());
        }
    }
}


