package com.example.proyecto.controller;

import com.example.proyecto.Main;
import com.example.proyecto.model.Abandono;
import com.example.proyecto.model.Usuario;
import com.example.proyecto.service.AbandonoService;
import com.example.proyecto.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class GestionUsuariosController {

    private String rolActual = "usuario";
    private String documentoActual = null;

    @FXML private Button btnEliminar;
    @FXML private Button btnEstadisticas;
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
    @FXML private TableColumn<Usuario, String> documentoColumn;
    @FXML private TableColumn<Usuario, Boolean> abandonadoColumn;
    @FXML private ComboBox<String> sexoComboBox;
    @FXML private ComboBox<String> objetivoComboBox;
    @FXML private TextField nombreField;
    @FXML private TextField pesoField;
    @FXML private TextField alturaField;
    @FXML private TextField documentoField;
    @FXML private StackPane contenidoStack;

    // Elementos para vista de abandonos
    @FXML private TableView<Abandono> tablaAbandonos;
    @FXML private TableColumn<Abandono, Integer> colId;
    @FXML private TableColumn<Abandono, String> colNombreAbandono;
    @FXML private TableColumn<Abandono, LocalDate> colFecha;
    @FXML private TableColumn<Abandono, String> colMotivo;

    private final UsuarioService service = new UsuarioService();
    private final AbandonoService abandonoService = new AbandonoService();
    private final ObservableList<Abandono> listaAbandonos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        edadColumn.setCellValueFactory(new PropertyValueFactory<>("edad"));
        pesoColumn.setCellValueFactory(new PropertyValueFactory<>("peso"));
        alturaColumn.setCellValueFactory(new PropertyValueFactory<>("altura"));
        objetivoColumn.setCellValueFactory(new PropertyValueFactory<>("objetivo"));
        caloriasColumn.setCellValueFactory(new PropertyValueFactory<>("calorias"));
        sexoColumn.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
        abandonadoColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isAbandonado())
        );

        // Configurar columnas de abandonos
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colNombreAbandono.setCellValueFactory(data -> data.getValue().nombreUsuarioProperty());
        colFecha.setCellValueFactory(data -> data.getValue().fechaAbandonoProperty());
        colMotivo.setCellValueFactory(data -> data.getValue().motivoProperty());

        tablaAbandonos.setItems(listaAbandonos);

        actividadBox.getItems().addAll(
                "Sedentario", "Ligero", "Moderado", "Intenso", "Muy intenso"
        );
        sexoComboBox.getItems().addAll("Masculino", "Femenino");
        objetivoComboBox.getItems().addAll(
                "Perder grasa", "Ganar masa muscular", "Mantener peso"
        );

        usuariosTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> { if (newVal != null) cargarEnFormulario(newVal); }
        );
    }

    public void setDocumentoActual(String documento) {
        this.documentoActual = documento;
    }

    public void setRol(String rol) {
        this.rolActual = rol;
        configurarVistaPorRol();
        cargarDatos();
    }

    private void configurarVistaPorRol() {
        if (rolActual.equals("usuario")) {
            btnEliminar.setVisible(false);
            btnEstadisticas.setVisible(false);
            btnEliminar.setManaged(false);
            btnEstadisticas.setManaged(false);
        }
    }

    private void cargarEnFormulario(Usuario u) {
        nombreField.setText(u.getNombre());
        edadField.setText(String.valueOf(u.getEdad()));
        pesoField.setText(String.valueOf(u.getPeso()));
        alturaField.setText(String.valueOf(u.getAltura()));
        sexoComboBox.setValue(u.getSexo());
        objetivoComboBox.setValue(u.getObjetivo());
        documentoField.setText(u.getDocumento() != null ? u.getDocumento() : "");
    }

    @FXML
    private void mostrarUsuarios() {
        if (!contenidoStack.getChildren().isEmpty()) {
            contenidoStack.getChildren().get(0).toFront();
        }
    }

    @FXML
    private void mostrarAbandonos() {
        cargarAbandonos();
        if (!contenidoStack.getChildren().isEmpty() && contenidoStack.getChildren().size() > 1) {
            contenidoStack.getChildren().get(1).toFront();
        }
    }

    private void cargarAbandonos() {
        try {
            listaAbandonos.clear();
            listaAbandonos.addAll(abandonoService.cargar());
        } catch (IOException e) {
            mostrarAlerta("Error al cargar abandonos: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarAbandono() {
        Abandono seleccionado = tablaAbandonos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un registro para eliminar.");
            return;
        }

        try {
            listaAbandonos.remove(seleccionado);
            abandonoService.guardar(listaAbandonos);
        } catch (IOException e) {
            mostrarAlerta("Error al eliminar abandono: " + e.getMessage());
        }
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
            String documento = documentoField.getText().trim();

            if (documento.isEmpty()) {
                mostrarAlerta("El número de documento es obligatorio.");
                return;
            }

            double calorias = service.calcularCalorias(peso, altura, edad, sexo, actividad, objetivo);

            Usuario usuario = new Usuario(nombre, edad, peso, altura, objetivo, calorias, sexo, documento);
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
            seleccionado.setDocumento(documentoField.getText().trim());
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
        try {
            FXMLLoader loader = new FXMLLoader(
                    Main.class.getResource("/com/example/proyecto/Login.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usuariosTable.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(scene);
        } catch (IOException e) {
            mostrarAlerta("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void guardarDatos() {
        try {
            service.guardar(usuariosTable.getItems());
        } catch (IOException e) {
            mostrarAlerta("Error al guardar datos: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        try {
            List<Usuario> usuarios = service.cargar();
            if (rolActual.equals("usuario") && documentoActual != null) {
                usuarios.stream()
                        .filter(u -> documentoActual.equals(u.getDocumento()))
                        .forEach(u -> usuariosTable.getItems().add(u));
            } else {
                usuariosTable.getItems().addAll(usuarios);
            }
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
        documentoField.clear();
        actividadBox.setValue(null);
        sexoComboBox.setValue(null);
        objetivoComboBox.setValue(null);
    }
}

