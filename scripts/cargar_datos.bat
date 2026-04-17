@echo off
REM Script para ejecutar el archivo SQL en MySQL
REM Uso: Simplemente ejecuta este archivo

setlocal enabledelayedexpansion

echo.
echo ════════════════════════════════════════════════════════
echo    CARGANDO DATOS EN MySQL
echo ════════════════════════════════════════════════════════
echo.

REM Variables
set "MYSQL_HOST=172.30.16.52"
set "MYSQL_USER=kssoto29"
set "MYSQL_PASSWORD=67001429"
set "MYSQL_DB=gym_db"
set "SQL_FILE=scripts\cargar_datos_completo.sql"

echo Ejecutando script SQL...
echo Host: %MYSQL_HOST%
echo Usuario: %MYSQL_USER%
echo Base de datos: %MYSQL_DB%
echo.

REM Buscar mysql.exe en el PATH
where mysql >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ MySQL encontrado
    echo.
    mysql -h %MYSQL_HOST% -u %MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DB% < %SQL_FILE%
    if %errorlevel% equ 0 (
        echo.
        echo ════════════════════════════════════════════════════════
        echo ✅ DATOS CARGADOS EXITOSAMENTE
        echo ════════════════════════════════════════════════════════
        echo.
    ) else (
        echo.
        echo ════════════════════════════════════════════════════════
        echo ❌ ERROR AL EJECUTAR EL SCRIPT SQL
        echo ════════════════════════════════════════════════════════
        echo.
    )
) else (
    echo ❌ MySQL no encontrado en el PATH
    echo.
    echo Intenta instalando MySQL Client o agregando su ruta al PATH
    echo Por ejemplo: C:\Program Files\MySQL\MySQL Server 8.0\bin
    echo.
)

pause

