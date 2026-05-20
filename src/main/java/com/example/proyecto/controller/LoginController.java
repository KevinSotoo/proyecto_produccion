package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.model.Membresia;
import com.example.proyecto.service.CuentaUsuario;
import com.example.proyecto.service.CuentaService;
import com.example.proyecto.service.MembresiaService;
import com.example.proyecto.service.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final CuentaService cuentaService = new CuentaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final MembresiaService membresiaService = new MembresiaService();

    @FXML
    private void iniciarSesion() {
        String documento = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (documento.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Por favor ingresa documento y contraseña.");
            return;
        }

        // Renovar membresías antes de validar
        try {
            membresiaService.renovarTodasLasMembresias();
        } catch (Exception e) {
            System.out.println("⚠ Advertencia al renovar membresías: " + e.getMessage());
        }

        // Verificar admin hardcodeado (usuario universal)
        if (documento.equals("admin") && password.equals("1234")) {
            // Pasar 'admin' como documentoActual para evitar valores null en controladores
            abrirPantalla("/com/example/proyecto/GestionUsuarios.fxml", "admin", "admin");
            return;
        }

        // Verificar usuario en JSON (búsqueda por documento que es el username)
        CuentaUsuario cuenta = cuentaService.buscarCuenta(documento, password);
        if (cuenta != null) {
            if (cuenta.getRol().equals("usuario")) {
                // Validar membresía activa para usuarios
                try {
                    Usuario usuario = usuarioService.obtenerPorDocumento(documento);

                    if (usuario != null) {
                        List<Membresia> membresiasActivas = membresiaService.obtenerMembresiasActivasPorDocumento(usuario.getDocumento());
                        if (membresiasActivas.isEmpty()) {
                            errorLabel.setText("Tu membresía ha expirado. Contacta al administrador para renovarla.");
                            return;
                        }
                    }
                } catch (Exception e) {
                    errorLabel.setText("Error al validar membresía: " + e.getMessage());
                    return;
                }
            }
            abrirPantalla("/com/example/proyecto/VistaUsuario.fxml", cuenta.getRol(), documento);
        } else {
            errorLabel.setText("Documento o contraseña incorrectos.");
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
            System.err.println("✗ Error al abrir el sistema: " + e.getMessage());
            errorLabel.setText("Error al abrir el sistema: " + e.getMessage());
        }

    }
}