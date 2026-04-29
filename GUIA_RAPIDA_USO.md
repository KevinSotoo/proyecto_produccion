# 🚀 GUÍA RÁPIDA - Cómo Usar World Time API

## Paso 1: Verificar que Compiló Correctamente

```bash
# En la terminal del proyecto
cd C:\Users\sala7.FI-LABSISTEMAS.002\IdeaProjects\proyecto_produccion
.\mvnw.cmd clean compile
```

Espera a que termine sin errores (puede tomar 1-2 minutos la primera vez).

---

## Paso 2: Ejecutar el Ejemplo

Una vez compilado correctamente:

```bash
java -cp target/classes com.example.proyecto.EjemploWorldTimeAPI
```

**Salida esperada:**
```
=== EJEMPLO: World Time API Integration ===

1. Obteniendo fecha del servidor...
   ✓ Fecha del servidor: 2024-04-29

2. Obteniendo hora del servidor...
   ✓ Hora del servidor: 2024-04-29T15:30:45

3. Obteniendo zona horaria del servidor...
   ✓ Zona horaria: America/Bogota

4. Validando membresía con ID 1...
   ✓ ¿Membresía activa?: SÍ

5. Obteniendo membresías activas del usuario 1...
   ✓ Total de membresías activas: 2
     - Premium (Vencimiento: 2024-06-30)
     - Gold (Vencimiento: 2024-12-31)

6. Información de sincronización:
   Hora del servidor: 2024-04-29T15:30:45 (America/Bogota)

=== FIN DEL EJEMPLO ===
```

Si ves esto ✓, significa que la integración funcionó correctamente.

---

## Paso 3: Usar en tus Controllers

### Opción A: Copia y Pega (Más Fácil)

1. Abre: `src/main/java/com/example/proyecto/ejemplos/EjemplosIntegracionWorldTimeAPI.java`
2. Busca el ejemplo que necesitas (Ejemplo1, Ejemplo2, etc.)
3. Copia el código
4. Pégalo en tu controller y adapta

### Opción B: Llamadas Directas

**Ejemplo simple en LoginController:**

```java
// En tu método de login
new Thread(() -> {
    MembresiaService ms = new MembresiaService();
    boolean tieneMembresia = !ms.obtenerMembresiasActivasDelUsuario(usuarioId).isEmpty();
    
    Platform.runLater(() -> {
        if (tieneMembresia) {
            System.out.println("✓ Usuario con membresía activa");
            // Abrir pantalla principal
        } else {
            System.out.println("✗ Membresía vencida");
            // Mostrar pantalla de renovación
        }
    });
}).start();
```

---

## Paso 4: Métodos Disponibles

### En TimeService (para obtener fecha del servidor)

```java
import com.example.proyecto.service.TimeService;

// Obtener fecha (LocalDate)
LocalDate hoy = TimeService.obtenerFechaDelServidor();
System.out.println("Hoy: " + hoy); // Output: 2024-04-29

// Obtener fecha y hora (LocalDateTime)
LocalDateTime ahora = TimeService.obtenerHoraDelServidor();
System.out.println("Ahora: " + ahora); // Output: 2024-04-29T15:30:45

// Obtener zona horaria
String zona = TimeService.obtenerZonaHoraria();
System.out.println("Zona: " + zona); // Output: America/Bogota
```

### En MembresiaService (para validar membresías)

```java
import com.example.proyecto.service.MembresiaService;

MembresiaService ms = new MembresiaService();

// Validar si una membresía está activa
boolean esActiva = ms.validarMembresiaActiva(1); // ID de membresía
// true = Membresía activa, false = Vencida

// Obtener solo membresías activas de un usuario
List<Membresia> activas = ms.obtenerMembresiasActivasDelUsuario(5); // ID usuario
// Retorna lista vacía si no hay membresías activas

// Obtener información de sincronización
String info = ms.obtenerInfoServidor();
System.out.println(info); 
// Output: Hora del servidor: 2024-04-29T15:30:45 (America/Bogota)
```

---

## Paso 5: Ejemplos Prácticos

### Ejemplo 1: Mostrar Estado en Label

```java
@FXML Label labelEstado;

public void mostrarEstadoMembresia(int usuarioId) {
    new Thread(() -> {
        MembresiaService ms = new MembresiaService();
        var activas = ms.obtenerMembresiasActivasDelUsuario(usuarioId);
        
        Platform.runLater(() -> {
            if (!activas.isEmpty()) {
                labelEstado.setText("✓ Membresía Activa");
                labelEstado.setStyle("-fx-text-fill: green;");
            } else {
                labelEstado.setText("✗ Membresía Vencida");
                labelEstado.setStyle("-fx-text-fill: red;");
            }
        });
    }).start();
}
```

### Ejemplo 2: Alertar si Vence Pronto

```java
import java.time.temporal.ChronoUnit;

public void verificarSiVencePronto(int usuarioId) {
    new Thread(() -> {
        MembresiaService ms = new MembresiaService();
        var activas = ms.obtenerMembresiasActivasDelUsuario(usuarioId);
        LocalDate hoy = TimeService.obtenerFechaDelServidor();
        
        Platform.runLater(() -> {
            for (Membresia m : activas) {
                long dias = ChronoUnit.DAYS.between(hoy, m.getFechaVencimiento());
                if (dias <= 7) {
                    mostrarAlerta("Membresía vence en " + dias + " días");
                }
            }
        });
    }).start();
}
```

### Ejemplo 3: Validar en el Login

```java
private void handleLogin() {
    String documento = documentoField.getText();
    
    new Thread(() -> {
        // 1. Validar usuario
        Usuario u = usuarioService.obtenerPorDocumento(documento);
        if (u == null) {
            Platform.runLater(() -> 
                errorLabel.setText("Usuario no encontrado")
            );
            return;
        }
        
        // 2. Verificar membresía
        MembresiaService ms = new MembresiaService();
        var activas = ms.obtenerMembresiasActivasDelUsuario(u.getId());
        
        Platform.runLater(() -> {
            if (!activas.isEmpty()) {
                abrirPantallaPrincipal(u);
            } else {
                mostrarAlerta("Debe renovar su membresía");
            }
        });
    }).start();
}
```

---

## ⚠️ Importante: Siempre Usa Threads

❌ **NO HAGAS ESTO** (Bloqueará la UI):
```java
@FXML
private void handleClick() {
    // ❌ Esto congelará la interfaz
    LocalDate fecha = TimeService.obtenerFechaDelServidor();
    label.setText(fecha.toString());
}
```

✅ **HAZLO ASÍ** (Correcto):
```java
@FXML
private void handleClick() {
    // ✅ Ejecuta en thread separado
    new Thread(() -> {
        LocalDate fecha = TimeService.obtenerFechaDelServidor();
        
        // ✅ Actualiza UI en el thread de JavaFX
        Platform.runLater(() -> {
            label.setText(fecha.toString());
        });
    }).start();
}
```

---

## 🐛 Solución de Problemas

### Problema: No puedo importar TimeService
```
Error: Cannot find symbol TimeService
```

**Solución:**
- Agrega el import: `import com.example.proyecto.service.TimeService;`
- Asegúrate que el archivo está en: 
  `src/main/java/com/example/proyecto/service/TimeService.java`

### Problema: La API es lenta
```
Tarda más de 5 segundos en responder
```

**Solución:**
- Es normal la primera vez (hasta 5 segundos)
- Las siguientes pueden ser más rápidas
- Implementa caché si necesitas mejor performance
- Ver Ejemplo 8 en EjemplosIntegracionWorldTimeAPI.java

### Problema: Obtiene la hora local en lugar de la del servidor
```
Fecha del servidor: 2024-04-30 (pero debería ser 2024-04-29)
```

**Solución:**
- Verifica tu reloj local
- Si sigue diferente, revisa tu conexión a internet
- Puede ser que tu zona horaria sea diferente
- Verifica: `TimeService.obtenerZonaHoraria()`

### Problema: La aplicación se congela cuando llama a la API
```
Se queda congelada por unos segundos
```

**Solución:**
- Asegúrate de que está en un Thread separado
- Usa `Platform.runLater()` para actualizar UI
- NO llames TimeService en el thread principal

---

## 📊 Resumen de Archivos

| Archivo | Ubicación | Propósito |
|---------|-----------|----------|
| **TimeService.java** | `service/` | Consumidor de API |
| **MembresiaService.java** | `service/` | MODIFICADO: 3 métodos nuevos |
| **EjemploWorldTimeAPI.java** | `raíz` | Demostración |
| **EjemplosIntegracionWorldTimeAPI.java** | `ejemplos/` | 8 ejemplos prácticos |
| **WORLD_TIME_API_DOCUMENTACION.md** | `raíz` | Documentación completa |
| **ARQUITECTURA_WORLD_TIME_API.md** | `raíz` | Diagramas |

---

## ✅ Verificación Final

Para asegurarte que todo funciona:

1. ✓ Compilar: `.\mvnw.cmd clean compile`
2. ✓ Ejecutar ejemplo: `java -cp target/classes com.example.proyecto.EjemploWorldTimeAPI`
3. ✓ Ver salida correcta (sin errores)
4. ✓ Copiar ejemplo a tu controller
5. ✓ Probar en tu app JavaFX

¡Listo! 🎉

---

## 📞 Referencia Rápida

```java
// Importar
import com.example.proyecto.service.TimeService;
import com.example.proyecto.service.MembresiaService;
import javafx.application.Platform;
import java.time.LocalDate;

// Usar en thread separado
new Thread(() -> {
    MembresiaService ms = new MembresiaService();
    LocalDate fecha = TimeService.obtenerFechaDelServidor();
    boolean activa = ms.validarMembresiaActiva(1);
    
    Platform.runLater(() -> {
        // Actualizar UI
    });
}).start();
```

¿Necesitas ayuda con una integración específica? Revisa EjemplosIntegracionWorldTimeAPI.java 🚀

