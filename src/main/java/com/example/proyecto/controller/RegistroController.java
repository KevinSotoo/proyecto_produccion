package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.service.CuentaUsuario;
import com.example.proyecto.service.CuentaService;
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

public class RegistroController {

    @FXML private TextField usernameField;
    @FXML private TextField documentoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmarPasswordField;
    @FXML private Label mensajeLabel;

    private final CuentaService cuentaService = new CuentaService();
    private final UsuarioService usuarioService = new UsuarioService();

    @FXML
    private void registrar() {
        String username = usernameField.getText().trim();
        String documento = documentoField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmar = confirmarPasswordField.getText().trim();

        // Validaciones
        if (username.isEmpty() || documento.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        if (!password.equals(confirmar)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        if (password.length() < 4) {
            mostrarError("La contraseña debe tener al menos 4 caracteres.");
            return;
        }

        // ✅ VALIDACIÓN NUEVA: Verificar que el documento existe como usuario
        int usuarioId = usuarioService.obtenerIdPorDocumento(documento);
        if (usuarioId <= 0) {
            mostrarError("El documento no existe en el sistema. Por favor verifica el número de documento.");
            return;
        }

        // Verificar que el username no exista ya
        try {
            List<CuentaUsuario> cuentas = cuentaService.cargarCuentas();
            boolean existe = cuentas.stream()
                    .anyMatch(c -> c.getUsername().equalsIgnoreCase(username));

            if (existe || username.equalsIgnoreCase("admin")) {
                mostrarError("Ese nombre de usuario ya está en uso.");
                return;
            }

            // ✅ CAMBIO: Ahora crea la cuenta en BD con el usuario_id asociado
            cuentaService.guardarCuentaConUsuario(username, password, documento);

            mostrarExito("¡Cuenta creada exitosamente! Ya puedes iniciar sesión.");
            limpiarCampos();

        } catch (IOException e) {
            mostrarError("Error al registrar: " + e.getMessage());
        }
    }

    @FXML
    private void volverLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/Login.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarError("Error al volver al login: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        mensajeLabel.setStyle("-fx-text-fill: #E74C3C; -fx-font-size: 12px;");
        mensajeLabel.setText(mensaje);
    }

    private void mostrarExito(String mensaje) {
        mensajeLabel.setStyle("-fx-text-fill: #2ECC71; -fx-font-size: 12px;");
        mensajeLabel.setText(mensaje);
    }

    private void limpiarCampos() {
        usernameField.clear();
        documentoField.clear();
        passwordField.clear();
        confirmarPasswordField.clear();
    }
}