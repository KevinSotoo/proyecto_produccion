package com.example.proyecto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

import java.time.LocalDate;

public class Abandono {
    private final IntegerProperty id;
    private final IntegerProperty usuarioId;
    private final StringProperty nombreUsuario;
    private final ObjectProperty<LocalDate> fechaAbandono;
    private final StringProperty motivo;

    // Constructor sin argumentos para Jackson
    public Abandono() {
        this.id = new SimpleIntegerProperty(0);
        this.usuarioId = new SimpleIntegerProperty(0);
        this.nombreUsuario = new SimpleStringProperty("");
        this.fechaAbandono = new SimpleObjectProperty<>(LocalDate.now());
        this.motivo = new SimpleStringProperty("");
    }

    public Abandono(int id, String nombreUsuario, LocalDate fechaAbandono, String motivo) {
        this.id = new SimpleIntegerProperty(id);
        this.usuarioId = new SimpleIntegerProperty(0);
        this.nombreUsuario = new SimpleStringProperty(nombreUsuario);
        this.fechaAbandono = new SimpleObjectProperty<>(fechaAbandono);
        this.motivo = new SimpleStringProperty(motivo);
    }

    public Abandono(int id, int usuarioId, LocalDate fechaAbandono, String motivo) {
        this.id = new SimpleIntegerProperty(id);
        this.usuarioId = new SimpleIntegerProperty(usuarioId);
        this.nombreUsuario = new SimpleStringProperty("");
        this.fechaAbandono = new SimpleObjectProperty<>(fechaAbandono);
        this.motivo = new SimpleStringProperty(motivo);
    }

    @JsonProperty("id")
    public int getIdValue() { return id.get(); }

    @JsonProperty("id")
    public void setIdValue(int value) { this.id.set(value); }

    @JsonProperty("nombreUsuario")
    public String getNombreUsuarioValue() { return nombreUsuario.get(); }

    @JsonProperty("nombreUsuario")
    public void setNombreUsuarioValue(String value) { this.nombreUsuario.set(value); }

    @JsonProperty("fechaAbandono")
    public LocalDate getFechaAbandonoValue() { return fechaAbandono.get(); }

    @JsonProperty("fechaAbandono")
    public void setFechaAbandonoValue(LocalDate value) { this.fechaAbandono.set(value); }

    @JsonProperty("motivo")
    public String getMotivoValue() { return motivo.get(); }

    @JsonProperty("motivo")
    public void setMotivoValue(String value) { this.motivo.set(value); }

    // Métodos para las propiedades (para usar en TableView)
    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreUsuarioProperty() { return nombreUsuario; }
    public ObjectProperty<LocalDate> fechaAbandonoProperty() { return fechaAbandono; }
    public StringProperty motivoProperty() { return motivo; }

    // Getters
    public int getId() { return id.get(); }
    public int getUsuarioId() { return usuarioId.get(); }
    public String getNombreUsuario() { return nombreUsuario.get(); }
    public LocalDate getFechaAbandono() { return fechaAbandono.get(); }
    public String getMotivo() { return motivo.get(); }
}

