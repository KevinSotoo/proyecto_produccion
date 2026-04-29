# 🕐 INTEGRACIÓN WORLD TIME API - DOCUMENTACIÓN

## 📋 Resumen
Se ha integrado la **World Time API** en tu proyecto para sincronizar fechas y validar membresías con la hora del servidor en Bogotá.

---

## 🎯 Beneficios

✅ **Validación de membresías sincronizadas** - No depende del reloj local del cliente  
✅ **Prevención de fraudes** - Impossibile manipular fechas cambiando el reloj local  
✅ **Información en tiempo real** - Zona horaria y fecha exacta del servidor  
✅ **Integración sin librerías externas** - Solo usa Java nativo y Jackson (ya instalado)  

---

## 📦 Archivos Creados/Modificados

### 1. **TimeService.java** (NUEVO)
**Ubicación:** `src/main/java/com/example/proyecto/service/TimeService.java`

**Métodos principales:**
```java
// Obtiene la fecha actual del servidor
LocalDate obtenerFechaDelServidor()

// Obtiene la hora y fecha del servidor
LocalDateTime obtenerHoraDelServidor()

// Obtiene la zona horaria configurada
String obtenerZonaHoraria()
```

**API utilizada:**
- Endpoint: `http://worldtimeapi.org/api/timezone/America/Bogota`
- Método: GET
- Autenticación: NO requiere API Key
- Límite de llamadas: Gratuito (sin límite conocido)

---

### 2. **MembresiaService.java** (MODIFICADO)
Se agregaron los siguientes métodos:

```java
// Valida si una membresía está activa comparando con fecha del servidor
boolean validarMembresiaActiva(int membresiaId)

// Obtiene solo las membresías activas de un usuario
List<Membresia> obtenerMembresiasActivasDelUsuario(int usuarioId)

// Información de sincronización con el servidor
String obtenerInfoServidor()
```

---

## 🔧 Cómo Usar

### Caso 1: Validar si una membresía está activa
```java
MembresiaService service = new MembresiaService();
boolean esActiva = service.validarMembresiaActiva(1); // ID de membresía

if (esActiva) {
    System.out.println("✓ La membresía está activa");
} else {
    System.out.println("✗ La membresía ha vencido");
}
```

### Caso 2: Obtener solo membresías activas de un usuario
```java
MembresiaService service = new MembresiaService();
List<Membresia> activas = service.obtenerMembresiasActivasDelUsuario(5); // ID de usuario

for (Membresia m : activas) {
    System.out.println(m.getTipoMembresia() + " - Vence: " + m.getFechaVencimiento());
}
```

### Caso 3: Obtener fecha del servidor
```java
LocalDate fecha = TimeService.obtenerFechaDelServidor();
LocalDateTime fechaHora = TimeService.obtenerHoraDelServidor();
String zona = TimeService.obtenerZonaHoraria();

System.out.println("Hoy en el servidor: " + fecha);
System.out.println("Zona: " + zona);
```

---

## 🧪 Prueba de Integración

Puedes ejecutar el ejemplo:
```bash
java -cp target/classes com.example.proyecto.EjemploWorldTimeAPI
```

Este archivo contiene ejemplos de todos los métodos.

---

## 🌐 Respuesta de la API

Cuando consultas `http://worldtimeapi.org/api/timezone/America/Bogota`, la respuesta es:

```json
{
  "abbreviation": "COT",
  "client_ip": "203.0.113.1",
  "datetime": "2024-04-29T15:30:45.123456-05:00",
  "day_of_week": 1,
  "day_of_year": 120,
  "dst": false,
  "dst_from": null,
  "dst_offset": 0,
  "dst_until": null,
  "raw_offset": -18000,
  "timezone": "America/Bogota",
  "unixtime": 1714423845,
  "utc_datetime": "2024-04-29T20:30:45.123456Z",
  "utc_offset": "-05:00",
  "week_number": 18
}
```

---

## ⚙️ Configuración

La zona horaria está **hard-coded** en:
```java
private static final String API_URL = "http://worldtimeapi.org/api/timezone/America/Bogota";
```

Si necesitas cambiar de zona horaria, modifica esta URL. Ejemplos:
- `America/New_York`
- `Europe/Madrid`
- `Asia/Tokyo`
- `America/Mexico_City`

**Lista completa:** https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

---

## 🚀 Casos de Uso en tu Aplicación

### En el Login
```java
// Validar que el usuario tenga una membresía activa
MembresiaService ms = new MembresiaService();
if (ms.obtenerMembresiasActivasDelUsuario(usuarioId).isEmpty()) {
    mostrarMensaje("Su membresía ha expirado, debe renovarla");
}
```

### En Estadísticas
```java
// Mostrar solo usuarios con membresía activa
List<Membresia> membresiasActivas = ms.obtenerMembresiasActivasDelUsuario(usuarioId);
labelInfoMembresia.setText("Membresías activas: " + membresiasActivas.size());
```

### En Gestión de Usuarios (Admin)
```java
// Marcar en la tabla cuáles membresías están vencidas
for (Membresia m : todasLasMembresias) {
    if (!ms.validarMembresiaActiva(m.getId())) {
        // Mostrar en rojo o con diferente ícono
        mostrarAlerta("Membresía vencida: " + m.getTipoMembresia());
    }
}
```

---

## ⚠️ Consideraciones

1. **Llamadas a la API** - Se ejecutan en el mismo thread. Para no bloquear la UI de JavaFX, usa `Platform.runLater()` o ejecuta en un Thread separado.

2. **Timeout** - Configurado a 5 segundos. Si no hay respuesta, se usa la fecha local.

3. **Sin autenticación** - World Time API es pública y no requiere API Key.

4. **Caché recomendado** - Para no hacer demasiadas llamadas, puedes cachear el resultado durante algunos segundos.

---

## 📊 Ejemplo Completo en JavaFX

```java
public class MembresiaController {
    @FXML Label labelEstadoMembresia;
    
    public void cargarEstadoMembresia() {
        // Ejecutar en thread separado para no bloquear UI
        new Thread(() -> {
            MembresiaService ms = new MembresiaService();
            boolean activa = ms.validarMembresiaActiva(1);
            
            Platform.runLater(() -> {
                if (activa) {
                    labelEstadoMembresia.setText("✓ Membresía Activa");
                    labelEstadoMembresia.setStyle("-fx-text-fill: green;");
                } else {
                    labelEstadoMembresia.setText("✗ Membresía Vencida");
                    labelEstadoMembresia.setStyle("-fx-text-fill: red;");
                }
            });
        }).start();
    }
}
```

---

## 🔗 Recursos

- **World Time API:** https://worldtimeapi.org/
- **Documentación:** https://worldtimeapi.org/pages/schema
- **Zonas horarias:** https://en.wikipedia.org/wiki/List_of_tz_database_time_zones

---

## ✅ Próximos Pasos Recomendados

1. Integrar la validación en el **LoginController**
2. Mostrar estado de membresía en **VistaUsuarioController**
3. Crear alertas para membresías por vencer (ej: dentro de 7 días)
4. Agregar caché para evitar llamadas excesivas a la API

---

## 📝 Notas

- La integración NO afecta tu código existente
- Es totalmente opcional usarla
- Puedes agregar más funcionalidades según necesites
- Todo está documentado con JavaDoc

¡Listo para usar! 🚀

