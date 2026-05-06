# GUÍA: INTEGRACIÓN DE MONGODB EN EL PROYECTO GYM

## 1. INSTALACIÓN DE MONGODB

### Opciones de instalación:

**Opción A: MongoDB Community Edition (Local)**
- Descargar desde: https://www.mongodb.com/try/download/community
- Instalar en tu computadora
- MongoDB se ejecuta como servicio

**Opción B: MongoDB Atlas (En la nube - Recomendado para pruebas)**
- Crear cuenta en: https://www.mongodb.com/cloud/atlas
- Crear un cluster gratuito
- Obtener la cadena de conexión

**Opción C: Docker (Si tienes Docker instalado)**
```bash
docker run -d -p 27017:27017 --name mongodb mongo
```

---

## 2. INSTALACIÓN DE MONGODB COMPASS

MongoDB Compass es la interfaz gráfica para MongoDB

1. Descargar desde: https://www.mongodb.com/products/tools/compass
2. Instalar normalmente
3. Al abrir, conectarse a `mongodb://localhost:27017`

---

## 3. ESTRUCTURA DE LA BASE DE DATOS EN MONGODB

### Colecciones creadas:

```
gym_db/
├── usuarios          (Información de miembros del gimnasio)
├── cuentas           (Credenciales de acceso)
├── abandonos         (Registro de abandonos de membresía)
└── membresias        (Información de membresías activas)
```

### Estructura de documentos:

**Colección: usuarios**
```json
{
  "_id": ObjectId(),
  "nombre": "Kevin",
  "edad": 20,
  "peso": 76.0,
  "altura": 174.0,
  "objetivo": "Ganar masa muscular",
  "calorias": 2754.0,
  "sexo": "Masculino",
  "documento": "1025528936",
  "abandonado": true,
  "tipoMembresia": "Básica",
  "fechaRegistro": "2024-01-15T10:30:00"
}
```

**Colección: cuentas**
```json
{
  "_id": ObjectId(),
  "username": "admin",
  "password": "1234",
  "rol": "admin",
  "fechaCreacion": "2024-01-15T10:30:00"
}
```

**Colección: abandonos**
```json
{
  "_id": ObjectId(),
  "documento": "1025528936",
  "fechaAbandono": "2024-03-15",
  "motivo": "Falta de tiempo disponible",
  "fechaRegistro": "2024-03-15T14:20:00"
}
```

**Colección: membresias**
```json
{
  "_id": ObjectId(),
  "documento": "1025528936",
  "tipo": "Premium",
  "fechaInicio": "2024-01-15",
  "fechaVencimiento": "2024-04-15",
  "estado": "activa"
}
```

---

## 4. CREAR LA BASE DE DATOS EN MONGODB COMPASS

### Paso 1: Conectar a MongoDB
- Abrir MongoDB Compass
- Si usas Local: `mongodb://localhost:27017`
- Si usas Atlas: Usar la cadena de conexión generada

### Paso 2: Crear la base de datos
1. Click en "+" o "Create Database"
2. Database Name: `gym_db`
3. Collection Name: `usuarios`
4. Create Database

### Paso 3: Crear las demás colecciones
Repetir el proceso para:
- `cuentas`
- `abandonos`
- `membresias`

### Paso 4: Crear índices (Opcional, pero recomendado)
En MongoDB Compass, ir a cada colección y crear índices:

**Para usuarios:**
- Campo: `documento`
- Unique: Sí

**Para cuentas:**
- Campo: `username`
- Unique: Sí

---

## 5. USAR MONGODBSERVICE EN TU CÓDIGO JAVA

### Iniciando la conexión en Main.java:

```java
import com.example.proyecto.service.MongoDBService;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Conectar a MongoDB al iniciar la aplicación
        MongoDBService.conectar();
        
        // ... resto del código ...
    }

    @Override
    public void stop() throws Exception {
        // Desconectar de MongoDB al cerrar
        MongoDBService.desconectar();
        super.stop();
    }
}
```

### Ejemplos de uso:

```java
// Insertar un usuario
MongoDBService.insertarUsuario(
    "Juan", 25, 75.0, 180.0, 
    "Ganar masa", 2800.0, "Masculino", "1234567890"
);

// Obtener usuario por documento
Document usuario = MongoDBService.obtenerUsuarioPorDocumento("1234567890");
System.out.println("Usuario: " + usuario.getString("nombre"));

// Insertar cuenta
MongoDBService.insertarCuenta("juan_user", "password123", "usuario");

// Obtener cuenta
Document cuenta = MongoDBService.obtenerCuentaPorUsername("juan_user");
System.out.println("Rol: " + cuenta.getString("rol"));

// Registrar abandono
MongoDBService.insertarAbandono("1234567890", LocalDate.now(), "Motivo del abandono");

// Verificar estado de la BD
MongoDBService.verificarBaseDatos();

// Desconectar
MongoDBService.desconectar();
```

---

## 6. CARGAR DATOS INICIALES

Se proporciona la clase `CargadorDatosMongoDBgado` que:
1. Lee los archivos JSON del proyecto
2. Los inserta automáticamente en MongoDB

### Para ejecutar:

```bash
# Desde la carpeta del proyecto
java -cp target/classes:target/lib/* com.example.proyecto.CargadorDatosMongoDBgado
```

O desde el IDE:
1. Click derecho en `CargadorDatosMongoDBgado.java`
2. Run

---

## 7. COMPARACIÓN: MYSQL vs SQLITE vs MONGODB

| Aspecto | MySQL | SQLite | MongoDB |
|---------|-------|--------|---------|
| **Tipo** | Relacional | Relacional | NoSQL (Documento) |
| **Instalación** | Requiere servidor | Archivo local | Local o en nube |
| **Conexión** | TCP/IP | Archivo local | TCP/IP |
| **Escalabilidad** | Alta | Baja | Alta |
| **Estructura** | Tablas fijas | Tablas fijas | Documentos flexibles |
| **JSON Nativo** | No | No | Sí ✓ |

---

## 8. INTEGRACIÓN CON CONTROLLADORES

### En LoginController.java:

```java
import com.example.proyecto.service.MongoDBService;
import org.bson.Document;

// Para verificar credenciales
Document cuenta = MongoDBService.obtenerCuentaPorUsername(username);
if (cuenta != null && cuenta.getString("password").equals(password)) {
    // Login exitoso
}
```

### En GestionUsuariosController.java:

```java
// Obtener usuarios
List<Document> usuarios = MongoDBService.obtenerTodosUsuarios();

// Convertir a objetos Usuario (si es necesario)
for (Document doc : usuarios) {
    String nombre = doc.getString("nombre");
    String documento = doc.getString("documento");
    boolean abandonado = doc.getBoolean("abandonado");
    // ... agregar a tabla ...
}
```

---

## 9. SOLUCIÓN DE PROBLEMAS

**Error: "Connection refused"**
- Verificar que MongoDB está corriendo
- Si es local: `mongo` en terminal
- Si es Atlas: Verificar la cadena de conexión

**Error: "Cannot find _id"**
- MongoDB crea automáticamente `_id`, no necesita especificarse

**Error: "Document not found"**
- Verificar que el documento existe
- Usar `MongoDBService.verificarBaseDatos()` para ver el contenido

---

## 10. CONSULTAS ÚTILES EN MONGODB SHELL/COMPASS

```javascript
// Ver todos los usuarios
db.usuarios.find()

// Buscar un usuario específico
db.usuarios.findOne({ "documento": "1234567890" })

// Contar registros
db.usuarios.countDocuments()

// Actualizar usuario
db.usuarios.updateOne(
  { "documento": "1234567890" },
  { $set: { "tipoMembresia": "Premium" } }
)

// Eliminar usuario
db.usuarios.deleteOne({ "documento": "1234567890" })

// Listar todos con filtro
db.usuarios.find({ "abandonado": false })
```

---

## RESUMEN

✓ MongoDB está integrado en tu proyecto  
✓ Se proporcionan 4 colecciones principales  
✓ Todas las operaciones CRUD están implementadas  
✓ Los datos JSON se cargan automáticamente  
✓ Es una alternativa flexible a MySQL/SQLite  

¿Preguntas o necesitas ayuda adicional?

