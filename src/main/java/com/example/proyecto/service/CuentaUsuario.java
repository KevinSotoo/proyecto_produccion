package com.example.proyecto.service;

public class CuentaUsuario {

    private String username;
    private String password;
    private String rol; // "admin" o "usuario"

    public CuentaUsuario() {}

    public CuentaUsuario(String username, String password, String rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRol() { return rol; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(String rol) { this.rol = rol; }
}