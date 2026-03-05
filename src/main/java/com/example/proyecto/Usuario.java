package com.example.proyecto;

public class Usuario {

    private String nombre;
    private int edad;
    private double peso;
    private double altura;
    private String objetivo;
    private double calorias;

    public Usuario(String nombre, int edad, double peso, double altura, String objetivo, double calorias) {
        this.nombre = nombre;
        this.edad = edad;
        this.peso = peso;
        this.altura = altura;
        this.objetivo = objetivo;
        this.calorias = calorias;
    }

    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public double getPeso() { return peso; }
    public double getAltura() { return altura; }
    public String getObjetivo() { return objetivo; }
    public double getCalorias() { return calorias; }
}