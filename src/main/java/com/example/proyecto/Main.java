package com.example.proyecto;

import com.example.proyecto.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Prueba la conexión a la BD
        System.out.println("Iniciando aplicación...");
        DatabaseConnection.testConnection();
        System.out.println(DatabaseConnection.getConnectionInfo());

        FXMLLoader loader = new FXMLLoader(
                Main.class.getResource("/com/example/proyecto/Login.fxml")
        );
        Scene scene = new Scene(loader.load());
        stage.setTitle("Sistema Gimnasio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}