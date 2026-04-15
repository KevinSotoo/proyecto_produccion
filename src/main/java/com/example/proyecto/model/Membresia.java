package com.example.proyecto.model;

import java.time.LocalDate;

public class Membresia {
    private int id;
    private int usuarioId;
    private String tipoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private double precio;
    private String estado;
    private String fechaRegistro; // Puede ser LocalDateTime si lo prefieres

    public Membresia() {}

    public Membresia(int id, int usuarioId, String tipoMembresia, LocalDate fechaInicio, LocalDate fechaVencimiento, double precio, String estado, String fechaRegistro) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.tipoMembresia = tipoMembresia;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.precio = precio;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getTipoMembresia() { return tipoMembresia; }
    public void setTipoMembresia(String tipoMembresia) { this.tipoMembresia = tipoMembresia; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}

