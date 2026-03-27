package com.example.proyecto.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Abandono {
    private IntegerProperty id;
    private StringProperty nombreUsuario;
    private ObjectProperty<LocalDate> fechaAbandono;
    private StringProperty motivo;

    public Abandono(int id, String nombreUsuario, LocalDate fechaAbandono, String motivo) {
        this.id = new SimpleIntegerProperty(id);
        this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
        this.fechaAbandono = new SimpleObjectProperty<>(fechaAbandono);
        this.motivo = new SimpleStringProperty(motivo);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreUsuarioProperty() { return nombreUsuario; }
    public ObjectProperty<LocalDate> fechaAbandonoProperty() { return fechaAbandono; }
    public StringProperty motivoProperty() { return motivo; }
}