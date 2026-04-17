-- Script para verificar y corregir la estructura de la base de datos
-- Ejecutar en MySQL con: mysql -h 172.30.16.52 -u kssoto29 -p67001429 gym_db < verificar_bd.sql

-- 1. Ver la estructura actual de la tabla usuarios
DESCRIBE usuarios;

-- 2. Si la altura está como DOUBLE, necesitamos escalar los datos
-- La altura en cm (174 cm) debe ser en metros (1.74 m) para DECIMAL(5,2)

-- 3. Ver datos actuales
SELECT id, nombre, altura FROM usuarios LIMIT 5;

-- 4. Convertir altura de cm a metros si es necesario
-- UPDATE usuarios SET altura = altura / 100 WHERE altura > 10;

-- 5. Crear tabla correcta si no existe
CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  documento VARCHAR(20),
  nombre VARCHAR(100),
  edad INT,
  sexo VARCHAR(20),
  peso DECIMAL(5,2),
  altura DECIMAL(5,2),
  objetivo VARCHAR(100),
  calorias DECIMAL(10,2),
  tipo_membresia VARCHAR(50),
  abandonado BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS abandonos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  fecha_abandono DATE,
  motivo VARCHAR(255),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cuentas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE,
  password VARCHAR(255),
  rol VARCHAR(50),
  usuario_id INT,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS membresias (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100),
  precio DECIMAL(8,2),
  duracion_meses INT
);

-- Verificar estructura de cuentas
DESCRIBE cuentas;

-- Ver registros actuales
SELECT * FROM cuentas;

-- Si necesitas hacer la columna usuario_id nullable (para admin)
-- ALTER TABLE cuentas MODIFY COLUMN usuario_id INT NULL;
-- ALTER TABLE cuentas MODIFY COLUMN email VARCHAR(255) NULL;
