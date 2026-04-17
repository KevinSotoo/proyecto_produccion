-- ════════════════════════════════════════════════════════════════════════════════
-- SCRIPT COMPLETO PARA CARGAR DATOS EN MySQL
-- Base de datos: gym_db
-- Usuario: kssoto29
-- Host: 172.30.16.52
-- ════════════════════════════════════════════════════════════════════════════════

-- Usar la base de datos
USE gym_db;

-- ════════════════════════════════════════════════════════════════════════════════
-- 1. ELIMINAR TABLAS ANTIGUAS (SI EXISTEN)
-- ════════════════════════════════════════════════════════════════════════════════
DROP TABLE IF EXISTS cuentas;
DROP TABLE IF EXISTS abandonos;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS membresias;

-- ════════════════════════════════════════════════════════════════════════════════
-- 2. CREAR TABLAS NUEVAS
-- ════════════════════════════════════════════════════════════════════════════════

-- Tabla usuarios
CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  documento VARCHAR(20) UNIQUE,
  nombre VARCHAR(100) NOT NULL,
  edad INT,
  sexo VARCHAR(20),
  peso DECIMAL(5,2),
  altura DECIMAL(5,2),
  objetivo VARCHAR(100),
  calorias DECIMAL(10,2),
  tipo_membresia VARCHAR(50) DEFAULT 'Básica',
  abandonado BOOLEAN DEFAULT FALSE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla abandonos
CREATE TABLE abandonos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT NOT NULL,
  fecha_abandono DATE,
  motivo VARCHAR(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla cuentas
CREATE TABLE cuentas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol VARCHAR(50),
  usuario_id INT,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Tabla membresias
CREATE TABLE membresias (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100),
  precio DECIMAL(8,2),
  duracion_meses INT
);

-- ════════════════════════════════════════════════════════════════════════════════
-- 3. INSERTAR DATOS DE USUARIOS (ALTURA CONVERTIDA DE CM A METROS)
-- ════════════════════════════════════════════════════════════════════════════════
INSERT INTO usuarios (documento, nombre, edad, sexo, peso, altura, objetivo, calorias, tipo_membresia) VALUES
('1025528936', 'Kevin', 20, 'Masculino', 76.0, 1.74, 'Ganar masa muscular', 2754.0, 'Básica'),
('1938475620', 'Daniel', 22, 'Masculino', 65.0, 1.80, 'Ganar masa muscular', 2404.0, 'Básica'),
('5673829104', 'Sebastian', 19, 'Masculino', 50.0, 1.84, 'Ganar masa muscular', 1917.0, 'Básica'),
('9081726354', 'Nicolas', 21, 'Masculino', 71.0, 1.74, 'Ganar masa muscular', 2776.0, 'Básica'),
('3748291056', 'Andres', 23, 'Masculino', 90.0, 1.78, 'Perder grasa', 2541.0, 'Básica'),
('6519203847', 'Camila', 22, 'Femenino', 58.0, 1.62, 'Mantener peso', 1980.0, 'Básica'),
('2847561930', 'Valeria', 20, 'Femenino', 52.0, 1.58, 'Ganar masa muscular', 2150.0, 'Básica'),
('7192038456', 'Miguel', 25, 'Masculino', 78.0, 1.76, 'Mantener peso', 2620.0, 'Básica'),
('4829105736', 'Laura', 19, 'Femenino', 61.0, 1.65, 'Perder grasa', 1750.0, 'Básica'),
('9301758462', 'Santiago', 24, 'Masculino', 95.0, 1.82, 'Perder grasa', 2890.0, 'Básica'),
('2758491036', 'Isabella', 21, 'Femenino', 55.0, 1.60, 'Mantener peso', 1870.0, 'Básica'),
('8162039475', 'Juan', 28, 'Masculino', 82.0, 1.79, 'Ganar masa muscular', 3100.0, 'Básica'),
('5039182746', 'Daniela', 22, 'Femenino', 67.0, 1.68, 'Perder grasa', 1920.0, 'Básica'),
('6928471503', 'Carlos', 30, 'Masculino', 88.0, 1.75, 'Mantener peso', 2710.0, 'Básica'),
('1475938206', 'Sofia', 18, 'Femenino', 49.0, 1.55, 'Ganar masa muscular', 2040.0, 'Básica'),
('8745201936', 'Mateo', 26, 'Masculino', 73.0, 1.77, 'Mantener peso', 2560.0, 'Básica'),
('3910284756', 'Alejandra', 23, 'Femenino', 70.0, 1.70, 'Perder grasa', 1830.0, 'Básica'),
('6283749105', 'Natalia', 20, 'Femenino', 54.0, 1.61, 'Mantener peso', 1790.0, 'Básica'),
('9502847613', 'Manuel', 30, 'Masculino', 80.0, 1.70, 'Perder grasa', 1661.0, 'Básica'),
('2047591836', 'Ana', 22, 'Femenino', 57.0, 1.60, 'Ganar masa muscular', 2186.0, 'Básica'),
('7639182504', 'Luis', 20, 'Masculino', 79.0, 1.79, 'Ganar masa muscular', 3211.0, 'Básica');

-- ════════════════════════════════════════════════════════════════════════════════
-- 4. INSERTAR DATOS DE ABANDONOS (BÚSQUEDA POR DOCUMENTO)
-- ════════════════════════════════════════════════════════════════════════════════
INSERT INTO abandonos (usuario_id, fecha_abandono, motivo) VALUES
((SELECT id FROM usuarios WHERE documento = '1025528936' LIMIT 1), '2024-03-15', 'Falta de tiempo disponible para asistir al gimnasio');

-- Marcar a Kevin como abandonado
UPDATE usuarios SET abandonado = TRUE WHERE documento = '1025528936';

-- ════════════════════════════════════════════════════════════════════════════════
-- 5. INSERTAR DATOS DE CUENTAS
-- ════════════════════════════════════════════════════════════════════════════════
INSERT INTO cuentas (username, password, rol) VALUES
('admin', '1234', 'admin');

INSERT INTO cuentas (username, password, rol, usuario_id) VALUES
('danielgamplays', 'daniela', 'usuario', NULL),
('1025528936', 'holahola', 'usuario', (SELECT id FROM usuarios WHERE documento = '1025528936' LIMIT 1)),
('1938475620', 'aloalo', 'usuario', (SELECT id FROM usuarios WHERE documento = '1938475620' LIMIT 1)),
('5673829104', '1234', 'usuario', (SELECT id FROM usuarios WHERE documento = '5673829104' LIMIT 1));

-- ════════════════════════════════════════════════════════════════════════════════
-- 6. INSERTAR DATOS DE MEMBRESIAS (OPCIONALES)
-- ════════════════════════════════════════════════════════════════════════════════
INSERT INTO membresias (nombre, precio, duracion_meses) VALUES
('Básica', 50.00, 1),
('Premium', 100.00, 3),
('Anual', 300.00, 12);

-- ════════════════════════════════════════════════════════════════════════════════
-- 7. VERIFICAR DATOS INSERTADOS
-- ════════════════════════════════════════════════════════════════════════════════
SELECT '✓ USUARIOS' as 'Tabla';
SELECT COUNT(*) as 'Total Registros' FROM usuarios;
SELECT id, documento, nombre, altura FROM usuarios LIMIT 5;

SELECT '✓ ABANDONOS' as 'Tabla';
SELECT COUNT(*) as 'Total Registros' FROM abandonos;
SELECT * FROM abandonos;

SELECT '✓ CUENTAS' as 'Tabla';
SELECT COUNT(*) as 'Total Registros' FROM cuentas;
SELECT id, username, rol, usuario_id FROM cuentas;

SELECT '✓ MEMBRESIAS' as 'Tabla';
SELECT COUNT(*) as 'Total Registros' FROM membresias;

-- ════════════════════════════════════════════════════════════════════════════════
-- FIN DEL SCRIPT
-- ════════════════════════════════════════════════════════════════════════════════


