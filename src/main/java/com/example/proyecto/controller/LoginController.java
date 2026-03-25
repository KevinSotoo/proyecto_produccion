package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.CuentaUsuario;
import com.example.proyecto.service.CuentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final CuentaService cuentaService = new CuentaService();

    @FXML
    private void iniciarSesion() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor ingresa usuario y contraseña.");
            return;
        }

        // Verificar admin hardcodeado
        if (username.equals("admin") && password.equals("1234")) {
            abrirPantalla("/com/example/proyecto/GestionUsuarios.fxml", "admin", null);
            return;
        }

        // Verificar usuario en JSON
        CuentaUsuario cuenta = cuentaService.buscarCuenta(username, password);
        if (cuenta != null) {
            abrirPantalla("/com/example/proyecto/GestionUsuarios.fxml", cuenta.getRol(), cuenta.getUsername());
        } else {
            errorLabel.setText("Usuario o contraseña incorrectos.");
        }
    }
    @FXML
    private void irRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/Registro.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            errorLabel.setText("Error al abrir registro: " + e.getMessage());
        }
    }

    private void abrirPantalla(String fxml, String rol, String documento) {
        try {
            if (rol.equals("usuario")) {
                // Abrir vista de usuario
                FXMLLoader loader = new FXMLLoader(
                        Main.class.getResource("/com/example/proyecto/VistaUsuario.fxml")
                );
                Scene scene = new Scene(loader.load());
                VistaUsuarioController controller = loader.getController();
                controller.setDocumento(documento);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setMaximized(false);
                stage.setScene(scene);
                stage.setMaximized(true);
            } else {
                // Abrir vista de admin
                FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml));
                Scene scene = new Scene(loader.load());
                GestionUsuariosController controller = loader.getController();
                controller.setRol(rol);
                controller.setDocumentoActual(documento);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setMaximized(false);
                stage.setScene(scene);
                stage.setMaximized(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error al abrir el sistema: " + e.getMessage());
        }

    }
}