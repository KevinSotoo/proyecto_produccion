# 🏗️ ARQUITECTURA - World Time API Integration

## Flujo de Datos

```
┌─────────────────────────────────────────────────────────────────┐
│                    TU APLICACIÓN JAVAFX                         │
│  (LoginController, VistaUsuarioController, etc.)                │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              MembresiaService (MODIFICADO)                       │
│  ✓ validarMembresiaActiva()                                     │
│  ✓ obtenerMembresiasActivasDelUsuario()                         │
│  ✓ obtenerInfoServidor()                                        │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              TimeService (NUEVO)                                 │
│  ✓ obtenerFechaDelServidor()                                    │
│  ✓ obtenerHoraDelServidor()                                     │
│  ✓ obtenerZonaHoraria()                                         │
│  ✓ consumirAPI() [PRIVADO]                                      │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────────┐
        │   WORLD TIME API               │
        │   http://worldtimeapi.org/...  │
        │                                │
        │   Respuesta JSON:              │
        │   {                            │
        │     "datetime": "...",         │
        │     "timezone": "...",         │
        │     "utc_datetime": "..."      │
        │   }                            │
        └────────────────────────────────┘
```

---

## Validación de Membresía - Diagrama de Secuencia

```
Usuario App        MembresiaService      TimeService      API World Time
   │                    │                   │                    │
   │ validar()          │                   │                    │
   ├───────────────────>│                   │                    │
   │                    │ obtenerFecha()    │                    │
   │                    ├──────────────────>│                    │
   │                    │                   │ consumirAPI()      │
   │                    │                   ├───────────────────>│
   │                    │                   │                    │
   │                    │                   │<─ respuesta JSON ──┤
   │                    │<─ LocalDate ──────┤                    │
   │                    │                   │                    │
   │                    │ (compara con BD)  │                    │
   │                    │                   │                    │
   │<─ boolean resultado─┤                   │                    │
   │                    │                   │                    │
```

---

## Estructura de Directorios

```
proyecto_produccion/
├── src/main/java/com/example/proyecto/
│   ├── service/
│   │   ├── MembresiaService.java         [MODIFICADO]
│   │   ├── TimeService.java              [NUEVO ⭐]
│   │   ├── UsuarioService.java
│   │   ├── AbandonoService.java
│   │   └── CuentaService.java
│   ├── controller/
│   │   ├── LoginController.java
│   │   ├── VistaUsuarioController.java
│   │   └── ...
│   ├── model/
│   │   ├── Membresia.java
│   │   ├── Usuario.java
│   │   └── ...
│   ├── util/
│   │   ├── DatabaseConnection.java
│   │   └── ...
│   ├── EjemploWorldTimeAPI.java          [NUEVO - EJEMPLO ⭐]
│   └── Main.java
│
├── WORLD_TIME_API_DOCUMENTACION.md       [NUEVO - DOCS ⭐]
└── pom.xml
```

---

## Ejemplo: Flujo Completo en el Login

```java
// LoginController.java
@FXML
private void handleLogin() {
    String usuario = textFieldUsuario.getText();
    String password = passwordField.getText();
    
    // 1. Validar credenciales
    Usuario u = usuarioService.validarLogin(usuario, password);
    
    if (u != null) {
        // 2. Verificar membresía activa
        MembresiaService ms = new MembresiaService();
        List<Membresia> activas = ms.obtenerMembresiasActivasDelUsuario(u.getId());
        
        if (!activas.isEmpty()) {
            // ✓ Usuario tiene membresía activa
            // -> Acceso garantizado
            cargarPantallaPrincipal();
        } else {
            // ✗ Usuario sin membresía activa
            // -> Mostrar pantalla de renovación
            mostrarDialogoRenovacion();
        }
    }
}
```

---

## Tabla de Comparación: Con vs Sin API

| Aspecto | Sin API | Con World Time API |
|--------|--------|-------------------|
| **Fecha utilizada** | Reloj local del cliente | Servidor sincronizado |
| **Puede engañar** | SÍ (cambiar reloj local) | NO (servidor confiable) |
| **Latencia** | Ninguna | ~100-500ms por llamada |
| **Confiabilidad** | ❌ Media | ✅ Alta |
| **Caso de uso** | Pruebas locales | Producción |

---

## Llamadas API por Feature

### Feature: Validar membresía en Login
```
1 llamada por login × N usuarios/día
Estimado: 1-5 MB de datos/mes
```

### Feature: Mostrar membresías activas
```
1 llamada cuando carga la pantalla
Estimado: <1 MB de datos/mes
```

### Feature: Actualizar estado en tiempo real
```
Recomendado: Cachear resultado por 5-10 minutos
Para evitar saturar la API
```

---

## Manejo de Errores

```
┌─ ¿Conexión a World Time API?
│  ├─ SÍ ─> Usar fecha del servidor
│  └─ NO ─> Usar fecha local + LOG WARNING
│
└─ ¿Respuesta válida?
   ├─ SÍ ─> Parsear JSON con Jackson
   └─ NO ─> Excepción capturada + fallback local
```

---

## Performance

| Operación | Tiempo estimado |
|-----------|----------------|
| Obtener fecha del servidor | 100-500ms |
| Validar membresía (con caché) | <1ms |
| Obtener membresías activas | 50-200ms (DB) + 100-500ms (API) |

**Recomendación:** Ejecutar en Thread separado en JavaFX

```java
new Thread(() -> {
    MembresiaService ms = new MembresiaService();
    boolean activa = ms.validarMembresiaActiva(1);
    
    // Actualizar UI en el thread de JavaFX
    Platform.runLater(() -> {
        labelEstado.setText(activa ? "Activa" : "Vencida");
    });
}).start();
```

---

## Integración con tu BD

```
┌─── BD Local (MySQL/SQLite)
│
├─ Tabla: membresias
│  ├─ id
│  ├─ usuario_id
│  ├─ fecha_vencimiento     ◄─── COMPARADA CON fecha del servidor
│  ├─ tipo_membresia
│  └─ estado
│
└─ Tabla: usuarios
   ├─ id
   └─ ...
```

**Lógica de validación:**
```
Si (fecha_vencimiento_BD >= fecha_servidor_API) {
    ✓ Membresía ACTIVA
} Sino {
    ✗ Membresía VENCIDA
}
```

---

## Seguridad

✅ **World Time API es pública y confiable**
- Endpoint: Sin autenticación requerida
- HTTPS: Soportado
- Datos: Solo lectura (no modifica nada)

✅ **Implementación segura**
- Timeout configurado (5 segundos)
- Manejo de excepciones
- Fallback a hora local si falla
- No se envían credenciales

⚠️ **Consideraciones**
- La API puede estar down (bajo, es pública)
- Latencia de red variable
- Cachear resultados para mejor performance

---

¡Listo! La integración está completa y lista para usar 🚀

