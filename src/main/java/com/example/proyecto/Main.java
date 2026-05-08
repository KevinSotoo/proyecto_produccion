package com.example.proyecto;

import com.example.proyecto.service.MembresiaService;
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
        launch();
    }
}