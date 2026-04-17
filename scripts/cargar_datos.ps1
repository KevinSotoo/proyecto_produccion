# Script PowerShell para ejecutar SQL en MySQL
# Este script ejecuta el SQL incluso si mysql.exe no está instalado

$MySQLHost = "172.30.16.52"
$MySQLUser = "kssoto29"
$MySQLPassword = "67001429"
$MySQLDB = "gym_db"
$SQLFile = "scripts/cargar_datos_completo.sql"

Write-Host "`n════════════════════════════════════════════════════════"
Write-Host "   CARGANDO DATOS EN MySQL"
Write-Host "════════════════════════════════════════════════════════`n"

Write-Host "Host: $MySQLHost"
Write-Host "Usuario: $MySQLUser"
Write-Host "Base de datos: $MySQLDB"
Write-Host ""

# Verificar si el archivo SQL existe
if (-not (Test-Path $SQLFile)) {
    Write-Host "❌ Error: Archivo SQL no encontrado: $SQLFile" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Archivo SQL encontrado: $SQLFile"
Write-Host ""

# Intentar usar mysql.exe si está disponible
$mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue
if ($mysqlPath) {
    Write-Host "Ejecutando con mysql.exe..."
    Write-Host ""
    & mysql -h $MySQLHost -u $MySQLUser -p$MySQLPassword $MySQLDB < $SQLFile

    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "════════════════════════════════════════════════════════"
        Write-Host "✅ DATOS CARGADOS EXITOSAMENTE" -ForegroundColor Green
        Write-Host "════════════════════════════════════════════════════════"
    } else {
        Write-Host ""
        Write-Host "════════════════════════════════════════════════════════"
        Write-Host "❌ ERROR AL EJECUTAR EL SCRIPT SQL" -ForegroundColor Red
        Write-Host "════════════════════════════════════════════════════════"
    }
} else {
    Write-Host "⚠️  mysql.exe no encontrado, usando método alternativo..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Opción 1: Instala MySQL Server o MySQL Client"
    Write-Host "Opción 2: Usa una herramienta gráfica como MySQL Workbench"
    Write-Host "Opción 3: Copia el contenido del archivo SQL y ejecuta en tu cliente MySQL:"
    Write-Host ""
    Write-Host "Archivo: $SQLFile"
    Write-Host ""

    # Mostrar las primeras líneas del SQL
    $sqlContent = Get-Content $SQLFile -Head 20
    Write-Host "Primeras líneas del script:"
    Write-Host "─────────────────────────────────────────────────────────"
    $sqlContent | ForEach-Object { Write-Host $_ }
    Write-Host "─────────────────────────────────────────────────────────"
}

Write-Host ""

