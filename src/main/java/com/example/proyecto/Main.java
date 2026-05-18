package com.example.proyecto;

import com.example.proyecto.service.MembresiaService;
import com.example.proyecto.service.MongoDBService;
import com.example.proyecto.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/com/example/proyecto/DbSelector.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Sistema Gimnasio");
        stage.setScene(scene);
        stage.show();

        // Verificar sincronización con servidor al iniciar
        verificarSincronizacion();
    }

    private void verificarSincronizacion() {
        new Thread(() -> {
            System.out.println("=== VERIFICACIÓN DE SINCRONIZACIÓN ===");

            MembresiaService membresiaService = new MembresiaService();
            try {
                String infoServidor = membresiaService.obtenerInfoServidor();
                System.out.println(infoServidor);
                System.out.println("✓ Conexión con World Time API: OK");
            } catch (Exception e) {
                System.out.println("✗ Error de sincronización: " + e.getMessage());
                System.out.println("⚠ Usando hora local como fallback");
            }
            System.out.println("=====================================\n");
        }).start();
    }

    public static void main(String[] args) {
        // Actualizar membresías y admin antes de iniciar la aplicación
        actualizarDatos();
        launch();
    }

    private static void actualizarDatos() {
        System.out.println("=== ACTUALIZANDO DATOS ===");

        MembresiaService membresiaService = new MembresiaService();

        // Para MySQL/SQLite
        try {
            DatabaseConnection.setEngine(DatabaseConnection.DatabaseEngine.MYSQL);
            System.out.println("Intentando actualizar MySQL...");
            membresiaService.renovarTodasLasMembresias();
        } catch (Exception e) {
            System.out.println("⚠ MySQL no disponible o error: " + e.getMessage());
        }

        try {
            DatabaseConnection.setEngine(DatabaseConnection.DatabaseEngine.SQLITE);
            System.out.println("Intentando actualizar SQLite...");
            membresiaService.renovarTodasLasMembresias();
        } catch (Exception e) {
            System.out.println("⚠ SQLite no disponible o error: " + e.getMessage());
        }

        // Para MongoDB
        try {
            DatabaseConnection.setEngine(DatabaseConnection.DatabaseEngine.MONGODB);
            System.out.println("Intentando conectar a MongoDB...");
            MongoDBService.conectar();
            System.out.println("✓ Conectado a MongoDB");
            MongoDBService.insertarCuentaAdminSiNoExiste();
            System.out.println("✓ Admin verificado en MongoDB");
            membresiaService.renovarTodasLasMembresias();
            MongoDBService.desconectar();
        } catch (Exception e) {
            System.out.println("⚠ MongoDB no disponible o error: " + e.getMessage());
        }

        System.out.println("✓ Actualización completada\n");
    }
}