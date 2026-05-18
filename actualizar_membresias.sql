-- Script para actualizar todas las membresías en SQLite
-- Ejecutar este script en la base de datos gym_db.db

-- Actualizar todas las membresías para que venzan en 30 días desde hoy (2026-06-16)
UPDATE membresias
SET fecha_vencimiento = '2026-06-16',
    estado = 'activa',
    fecha_inicio = '2026-05-17'
WHERE 1=1;

-- Verificar la actualización
SELECT COUNT(*) as total_membresias,
       COUNT(CASE WHEN fecha_vencimiento >= '2026-05-17' THEN 1 END) as activas,
       COUNT(CASE WHEN fecha_vencimiento < '2026-05-17' THEN 1 END) as vencidas
FROM membresias;

-- Ver las primeras 5 membresías
SELECT id, usuario_id, tipo_membresia, fecha_inicio, fecha_vencimiento, estado
FROM membresias
LIMIT 5;

