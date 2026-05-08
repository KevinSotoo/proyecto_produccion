package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.AbandonoService;
import com.example.proyecto.service.UsuarioService;
import com.example.proyecto.service.MembresiaService;
import com.example.proyecto.service.TimeService;
import com.example.proyecto.model.Membresia;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    @FXML private TextArea motivoCancelacionField;
    @FXML private Label estadoMembresiaLabel;
    @FXML private Label fechaVencimientoLabel;

    private String documentoActual;
    private Usuario usuarioActual;
    private final UsuarioService service = new UsuarioService();
    private final AbandonoService abandonoService = new AbandonoService();
    private final MembresiaService membresiaService = new MembresiaService();
    private final TimeService timeService = new TimeService();

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

                // Cargar estado de membresía
                cargarEstadoMembresia(usuarioActual);
            }
        } catch (IOException e) {
            mostrarAlerta("Error al cargar tus datos: " + e.getMessage());
        }
    }

    private void cargarEstadoMembresia(Usuario usuario) {
        new Thread(() -> {
            List<Membresia> membresiasActivas = membresiaService.obtenerMembresiasActivasDelUsuario(usuario.getId());

            javafx.application.Platform.runLater(() -> {
                if (!membresiasActivas.isEmpty()) {
                    Membresia m = membresiasActivas.get(0); // Primera membresía activa
                    estadoMembresiaLabel.setText("✓ " + m.getTipoMembresia().toUpperCase());
                    estadoMembresiaLabel.setStyle("-fx-text-fill: #00AA00; -fx-font-size: 14px; -fx-font-weight: bold;");
                    fechaVencimientoLabel.setText("Vence: " + m.getFechaVencimiento());

                    // Verificar si vence pronto
                    verificarMembresiaPorVencer(m);
                } else {
                    estadoMembresiaLabel.setText("✗ SIN MEMBRESÍA ACTIVA");
                    estadoMembresiaLabel.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 14px; -fx-font-weight: bold;");
                    fechaVencimientoLabel.setText("Renueva tu membresía");
                }
            });
        }).start();
    }

    private void verificarMembresiaPorVencer(Membresia membresia) {
        try {
            LocalDate hoy = TimeService.obtenerFechaDelServidor();
            long diasRestantes = ChronoUnit.DAYS.between(hoy, membresia.getFechaVencimiento());

            if (diasRestantes > 0 && diasRestantes <= 7) {
                // Membresía vence pronto
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta: Membresía por Vencer");
                alert.setHeaderText("¡Tu membresía está por expirar!");
                alert.setContentText(
                        "Tu membresía (" + membresia.getTipoMembresia() +
                                ") vence en " + diasRestantes + " días.\n\n" +
                                "Fecha: " + membresia.getFechaVencimiento() + "\n\n" +
                                "Considera renovarla para continuar disfrutando de nuestros servicios."
                );
                alert.showAndWait();
            }
        } catch (Exception e) {
            // Si hay error con la API, no mostrar alerta
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
    private void cancelarSuscripcion() {
        if (usuarioActual == null) {
            mostrarAlerta("No se encontró tu perfil.");
            return;
        }

        // Confirmación
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION);
        confirmar.setTitle("Cancelar Suscripción");
        confirmar.setHeaderText("¿Está seguro?");
        confirmar.setContentText("¿Desea cancelar su suscripción? Esta acción no se puede deshacer.");

        if (confirmar.showAndWait().orElse(null) != ButtonType.OK) {
            return;
        }

        try {
            String motivo = motivoCancelacionField.getText().trim();
            if (motivo.isEmpty()) {
                motivo = "No especificado";
            }

            // Marcar usuario como abandonado
            usuarioActual.setAbandonado(true);

            // Guardar cambios en usuarios
            List<Usuario> todos = service.cargar();
            for (int i = 0; i < todos.size(); i++) {
                if (documentoActual.equals(todos.get(i).getDocumento())) {
                    todos.set(i, usuarioActual);
                    break;
                }
            }
            service.guardar(todos);

            // Registrar abandono
            abandonoService.agregarAbandono(usuarioActual.getNombre(), motivo);

            mostrarExito("Suscripción cancelada. Gracias por usar nuestros servicios.");

            // Regresar al login después de 2 segundos
            Thread.sleep(1500);
            salir();

        } catch (Exception e) {
            mostrarAlerta("Error al cancelar suscripción: " + e.getMessage());
        }
    }

    @FXML
    private void mostrarPerfil() {
        // Este método se llama cuando se selecciona "Mi Perfil"
        // En una arquitectura más compleja, aquí cambiarías vistas usando StackPane
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
