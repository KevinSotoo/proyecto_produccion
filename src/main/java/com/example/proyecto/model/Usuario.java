package com.example.proyecto.model;

public class Usuario {

    private String nombre;
    private int edad;
    private double peso;
    private double altura;
    private String objetivo;
    private double calorias;
    private String sexo;
    private String documento;
    private boolean abandonado;

    public Usuario() {}

    public Usuario(String nombre, int edad, double peso, double altura, String objetivo, double calorias, String sexo, String documento) {
        this.nombre = nombre;
        this.edad = edad;
        this.peso = peso;
        this.altura = altura;
        this.objetivo = objetivo;
        this.calorias = calorias;
        this.sexo = sexo;
        this.documento = documento;
        this.abandonado = false;
    }

    public String getNombre() { return nombre; }
    public int getEdad() { return edad; }
    public double getPeso() { return peso; }
    public double getAltura() { return altura; }
    public String getObjetivo() { return objetivo; }
    public double getCalorias() { return calorias; }
    public String getSexo() { return sexo; }
    public String getDocumento() { return documento; }
    public boolean isAbandonado() { return abandonado; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setPeso(double peso) { this.peso = peso; }
    public void setAltura(double altura) { this.altura = altura; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public void setCalorias(double calorias) { this.calorias = calorias; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setAbandonado(boolean abandonado) { this.abandonado = abandonado; }
}