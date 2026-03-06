package com.example.proyecto;

public class Usuario {

    private String nombre;
    private int edad;
    private double peso;
    private double altura;
    private String objetivo;
    private double calorias;

    // Constructor vacío - necesario para Jackson
    public Usuario() {}

    public Usuario(String nombre, int edad, double peso, double altura, String objetivo, double calorias) {
        this.nombre = nombre;
        this.edad = edad;
        this.peso = peso;
        this.altura = altura;
        this.objetivo = objetivo;
        this.calorias = calorias;
    }

    // Getters
    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public double getPeso() { return peso; }
    public double getAltura() { return altura; }
    public String getObjetivo() { return objetivo; }
    public double getCalorias() { return calorias; }

    // Setters - necesarios para Jackson
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setPeso(double peso) { this.peso = peso; }
    public void setAltura(double altura) { this.altura = altura; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public void setCalorias(double calorias) { this.calorias = calorias; }
}