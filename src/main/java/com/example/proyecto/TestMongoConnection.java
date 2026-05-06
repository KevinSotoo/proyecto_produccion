package com.example.proyecto;

import com.example.proyecto.service.MongoDBService;

public class TestMongoConnection {
    public static void main(String[] args) {
        System.out.println("Probando conexión a MongoDB en 172.30.16.165:27017");

        // Intentar conectar
        MongoDBService.conectar();

        // Verificar base de datos
        MongoDBService.verificarBaseDatos();

        // Desconectar
        MongoDBService.desconectar();

        System.out.println("Prueba completada.");
    }
}
