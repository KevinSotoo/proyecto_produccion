package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.service.CuentaUsuario;
import com.example.proyecto.service.CuentaService;
import com.example.proyecto.service.UsuarioService;
import com.example.proyecto.service.MembresiaService;
import com.example.proyecto.model.Membresia;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class RegistroController {

    @FXML private TextField documentoField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmarPasswordField;
    @FXML private Label mensajeLabel;

    private final CuentaService cuentaService = new CuentaService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final MembresiaService membresiaService = new MembresiaService();

    @FXML
    private void registrar() {
        String documento = documentoField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmar = confirmarPasswordField.getText().trim();

        // Validaciones
        if (documento.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
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

        try {
            // ✅ Verificar que la cuenta no exista ya
            CuentaUsuario cuentaExistente = cuentaService.buscarCuentaPorDocumento(documento);
            if (cuentaExistente != null) {
                mostrarError("Este documento ya tiene una cuenta registrada.");
                return;
            }

            // ✅ Guardar la cuenta con el documento como username
            cuentaService.guardarCuentaConUsuario(documento, password, documento);

            // Crear membresía automáticamente con duración variable
            Random random = new Random();
            int dias = (random.nextInt(3) + 1) * 30; // 30, 60 o 90 días
            Membresia membresia = new Membresia(0, usuarioId, "Básica", LocalDate.now(), LocalDate.now().plusDays(dias), 100.0, "activa", LocalDateTime.now().toString());
            membresiaService.guardar(membresia);

            mostrarExito("¡Cuenta creada exitosamente! Ya puedes iniciar sesión.");
            limpiarCampos();

        } catch (Exception e) {
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
            Stage stage = (Stage) documentoField.getScene().getWindow();
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
        documentoField.clear();
        passwordField.clear();
        confirmarPasswordField.clear();
    }
}