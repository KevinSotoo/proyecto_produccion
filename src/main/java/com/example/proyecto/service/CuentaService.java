package com.example.proyecto.service;

import com.example.proyecto.model.CuentaUsuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CuentaService {

    private static final File ARCHIVO_CUENTAS = new File(
            System.getProperty("user.dir") + "/data/cuentas.json"
    );
    private final ObjectMapper mapper = new ObjectMapper();

    public List<CuentaUsuario> cargarCuentas() throws IOException {
        if (!ARCHIVO_CUENTAS.exists()) return new ArrayList<>();
        return mapper.readValue(ARCHIVO_CUENTAS, new TypeReference<List<CuentaUsuario>>() {});
    }

    public void guardarCuentas(List<CuentaUsuario> cuentas) throws IOException {
        ARCHIVO_CUENTAS.getParentFile().mkdirs();
        mapper.writerWithDefaultPrettyPrinter().writeValue(ARCHIVO_CUENTAS, cuentas);
    }

    public CuentaUsuario buscarCuenta(String username, String password) {
        try {
            List<CuentaUsuario> cuentas = cargarCuentas();
            return cuentas.stream()
                    .filter(c -> c.getUsername().equals(username) && c.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    public void registrarCuenta(CuentaUsuario cuenta) throws IOException {
        List<CuentaUsuario> cuentas = cargarCuentas();
        cuentas.add(cuenta);
        guardarCuentas(cuentas);
    }
}