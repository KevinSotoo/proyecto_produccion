/*
 * EJEMPLOS DE INTEGRACIÓN - World Time API
 * 
 * Este archivo muestra cómo integrar la World Time API en tus controllers
 * Copia y adapta estos ejemplos según tus necesidades.
 */

package com.example.proyecto.ejemplos;

import com.example.proyecto.service.TimeService;
import com.example.proyecto.service.MembresiaService;
import com.example.proyecto.service.UsuarioService;
import com.example.proyecto.model.Membresia;
import com.example.proyecto.model.Usuario;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * EJEMPLO 1: Validar Membresía en el Login
 * 
 * Ubicación recomendada: LoginController.java
 * 
 * Este ejemplo verifica si el usuario tiene una membresía activa
 * antes de permitir el acceso.
 */
class Ejemplo1_ValidarEnLogin {
    
    private MembresiaService membresiaService = new MembresiaService();
    private UsuarioService usuarioService = new UsuarioService();
    
    public void iniciarSesion(String documento) {
        // Ejecutar en thread separado para no bloquear UI
        new Thread(() -> {
            // 1. Obtener usuario
            Usuario usuario = usuarioService.obtenerPorDocumento(documento);
            
            if (usuario == null) {
                Platform.runLater(() -> {
                    mostrarError("Usuario no encontrado");
                });
                return;
            }
            
            // 2. Verificar membresía activa
            List<Membresia> membresiasActivas = 
                membresiaService.obtenerMembresiasActivasDelUsuario(usuario.getId());
            
            Platform.runLater(() -> {
                if (!membresiasActivas.isEmpty()) {
                    // ✓ Tiene membresía activa
                    mostrarExito("Bienvenido! Membresía válida");
                    abrirPantallaPrincipal(usuario);
                } else {
                    // ✗ Sin membresía activa
                    mostrarAlerta("Su membresía ha expirado", 
                        "Debe renovarla para continuar");
                    abrirPantallaRenovacion(usuario);
                }
            });
        }).start();
    }
    
    private void mostrarError(String msg) { /* ... */ }
    private void mostrarExito(String msg) { /* ... */ }
    private void mostrarAlerta(String titulo, String msg) { /* ... */ }
    private void abrirPantallaPrincipal(Usuario u) { /* ... */ }
    private void abrirPantallaRenovacion(Usuario u) { /* ... */ }
}

/**
 * EJEMPLO 2: Mostrar Estado de Membresía en Vista de Usuario
 * 
 * Ubicación recomendada: VistaUsuarioController.java
 * 
 * Muestra en la pantalla principal si la membresía está activa o vencida.
 */
class Ejemplo2_MostrarEstadoMembresia {
    
    private Label labelEstadoMembresia;  // @FXML
    private Label labelFechaVencimiento; // @FXML
    private MembresiaService membresiaService = new MembresiaService();
    
    public void cargarEstadoMembresia(int usuarioId) {
        new Thread(() -> {
            List<Membresia> activas = 
                membresiaService.obtenerMembresiasActivasDelUsuario(usuarioId);
            
            Platform.runLater(() -> {
                if (!activas.isEmpty()) {
                    Membresia m = activas.get(0); // Primera membresía activa
                    labelEstadoMembresia.setText("✓ MEMBRESÍA ACTIVA");
                    labelEstadoMembresia.setStyle("-fx-text-fill: #00AA00; -fx-font-size: 14;");
                    labelFechaVencimiento.setText("Vence: " + m.getFechaVencimiento());
                } else {
                    labelEstadoMembresia.setText("✗ MEMBRESÍA VENCIDA");
                    labelEstadoMembresia.setStyle("-fx-text-fill: #FF0000; -fx-font-size: 14;");
                    labelFechaVencimiento.setText("Debe renovar su membresía");
                }
            });
        }).start();
    }
}

/**
 * EJEMPLO 3: Alertar Membresías por Vencer
 * 
 * Ubicación recomendada: VistaUsuarioController.java (en initialize())
 * 
 * Muestra una alerta si la membresía vence en menos de 7 días.
 */
class Ejemplo3_AlertarPorVencer {
    
    private MembresiaService membresiaService = new MembresiaService();
    
    public void verificarMembresiasProximas(int usuarioId) {
        new Thread(() -> {
            List<Membresia> todasLasMembresias = 
                membresiaService.cargar();
            
            LocalDate hoy = TimeService.obtenerFechaDelServidor();
            
            Platform.runLater(() -> {
                for (Membresia m : todasLasMembresias) {
                    if (m.getUsuarioId() != usuarioId) continue;
                    
                    long diasRestantes = ChronoUnit.DAYS.between(hoy, m.getFechaVencimiento());
                    
                    if (diasRestantes > 0 && diasRestantes <= 7) {
                        // Membresía vence pronto
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Alerta: Membresía por Vencer");
                        alert.setContentText(
                            "Su membresía (" + m.getTipoMembresia() + 
                            ") vence en " + diasRestantes + " días.\n\n" +
                            "Fecha: " + m.getFechaVencimiento()
                        );
                        alert.showAndWait();
                    }
                }
            });
        }).start();
    }
}

/**
 * EJEMPLO 4: Tabla de Membresías con Estado (Admin)
 * 
 * Ubicación recomendada: GestionUsuariosController.java
 * 
 * Muestra todas las membresías con indicador visual de estado.
 */
class Ejemplo4_TablaMembresiasConEstado {
    
    private MembresiaService membresiaService = new MembresiaService();
    
    public String obtenerEstadoMembresia(int membresiaId) {
        // Esta función se puede usar en una celda de tabla
        if (membresiaService.validarMembresiaActiva(membresiaId)) {
            return "✓ ACTIVA";
        } else {
            return "✗ VENCIDA";
        }
    }
    
    public String obtenerColorEstado(int membresiaId) {
        // Para pintar la fila de diferente color
        if (membresiaService.validarMembresiaActiva(membresiaId)) {
            return "-fx-text-fill: green;";
        } else {
            return "-fx-text-fill: red;";
        }
    }
    
    // En tu TableView, usa:
    // tableColumn.setCellFactory(col -> new TableCell<Membresia, String>() {
    //     @Override
    //     protected void updateItem(String item, boolean empty) {
    //         super.updateItem(item, empty);
    //         if (empty || getTableRow().getItem() == null) {
    //             setText(null);
    //             setStyle("");
    //         } else {
    //             Membresia m = getTableRow().getItem();
    //             setText(obtenerEstadoMembresia(m.getId()));
    //             setStyle(obtenerColorEstado(m.getId()));
    //         }
    //     }
    // });
}

/**
 * EJEMPLO 5: Sincronización con Servidor al Iniciar App
 * 
 * Ubicación recomendada: Main.java (en start() method)
 * 
 * Muestra la información de sincronización en consola.
 */
class Ejemplo5_SincronizacionAlIniciar {
    
    private MembresiaService membresiaService = new MembresiaService();
    
    public void verificarSincronizacion() {
        System.out.println("=== VERIFICACIÓN DE SINCRONIZACIÓN ===");
        
        new Thread(() -> {
            try {
                String infoServidor = membresiaService.obtenerInfoServidor();
                System.out.println(infoServidor);
                System.out.println("✓ Conexión con World Time API: OK");
            } catch (Exception e) {
                System.out.println("✗ Error de sincronización: " + e.getMessage());
                System.out.println("⚠ Usando hora local como fallback");
            }
            System.out.println("=====================================\n");
        }).start();
    }
}

/**
 * EJEMPLO 6: Renovar Membresía con Validación de Servidor
 * 
 * Ubicación recomendada: GestionUsuariosController.java
 * 
 * Valida que la fecha de inicio sea posterior a hoy (según servidor).
 */
class Ejemplo6_RenovarMembresia {
    
    private MembresiaService membresiaService = new MembresiaService();
    
    public boolean renovarMembresia(Membresia m) {
        LocalDate hoyServidor = TimeService.obtenerFechaDelServidor();
        
        // Validar que la fecha de inicio sea hoy o posterior
        if (m.getFechaInicio().isBefore(hoyServidor)) {
            System.out.println("Error: La fecha de inicio no puede ser anterior a hoy");
            return false;
        }
        
        // Validar que la fecha de vencimiento sea posterior a la de inicio
        if (m.getFechaVencimiento().isBefore(m.getFechaInicio())) {
            System.out.println("Error: La fecha de vencimiento debe ser posterior a la de inicio");
            return false;
        }
        
        // Si pasa todas las validaciones, guardar
        membresiaService.guardar(m);
        System.out.println("✓ Membresía renovada exitosamente");
        return true;
    }
}

/**
 * EJEMPLO 7: Dashboard Admin con Estadísticas de Membresías
 * 
 * Ubicación recomendada: EstadisticasController.java
 * 
 * Muestra estadísticas sobre membresías activas, vencidas, etc.
 */
class Ejemplo7_DashboardMembresias {
    
    private MembresiaService membresiaService = new MembresiaService();
    
    public void cargarEstadisticas() {
        new Thread(() -> {
            List<Membresia> todasLasMembresias = membresiaService.cargar();
            LocalDate hoy = TimeService.obtenerFechaDelServidor();
            
            int activas = 0;
            int vencidas = 0;
            int porVencer = 0; // Dentro de 7 días
            
            for (Membresia m : todasLasMembresias) {
                if (m.getFechaVencimiento().isBefore(hoy)) {
                    vencidas++;
                } else if (ChronoUnit.DAYS.between(hoy, m.getFechaVencimiento()) <= 7) {
                    porVencer++;
                } else {
                    activas++;
                }
            }
            
            Platform.runLater(() -> {
                System.out.println("\n=== ESTADÍSTICAS DE MEMBRESÍAS ===");
                System.out.println("Membresías Activas: " + activas);
                System.out.println("Membresías por Vencer (7 días): " + porVencer);
                System.out.println("Membresías Vencidas: " + vencidas);
                System.out.println("Total: " + todasLasMembresias.size());
                System.out.println("==================================\n");
                
                // Actualizar labels en UI
                // labelActivas.setText(String.valueOf(activas));
                // labelProximas.setText(String.valueOf(porVencer));
                // labelVencidas.setText(String.valueOf(vencidas));
            });
        }).start();
    }
}

/**
 * EJEMPLO 8: Caché para Evitar Llamadas Excesivas
 * 
 * Ubicación recomendada: Crear clase TimeServiceCached.java
 * 
 * Evita hacer múltiples llamadas a la API en corto tiempo.
 */
class Ejemplo8_ConCaché {
    
    private static LocalDate fechaCacheada = null;
    private static long ultimaActualizacion = 0;
    private static final long CACHE_DURATION_MS = 300000; // 5 minutos
    
    public LocalDate obtenerFechaDelServidorConCaché() {
        long ahora = System.currentTimeMillis();
        
        if (fechaCacheada != null && 
            (ahora - ultimaActualizacion) < CACHE_DURATION_MS) {
            // Usar caché
            return fechaCacheada;
        }
        
        // Actualizar caché
        fechaCacheada = TimeService.obtenerFechaDelServidor();
        ultimaActualizacion = ahora;
        
        return fechaCacheada;
    }
}

/**
 * RESUMEN DE USO
 * 
 * 1. En LoginController:
 *    - Usar Ejemplo1 para validar membresía en login
 * 
 * 2. En VistaUsuarioController:
 *    - Usar Ejemplo2 para mostrar estado en pantalla principal
 *    - Usar Ejemplo3 para alertar membresías por vencer
 * 
 * 3. En GestionUsuariosController (Admin):
 *    - Usar Ejemplo4 para tabla con estado visual
 *    - Usar Ejemplo6 para renovación con validación
 * 
 * 4. En EstadisticasController:
 *    - Usar Ejemplo7 para dashboard
 * 
 * 5. En Main:
 *    - Usar Ejemplo5 al iniciar la aplicación
 * 
 * 6. Considerar Ejemplo8 si tienes muchas llamadas simultáneas
 */

