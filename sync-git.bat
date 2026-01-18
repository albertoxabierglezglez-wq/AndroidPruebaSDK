@echo off
cd /d "C:\MiAppAndroid"

echo === Sincronizando con GitHub ===
echo Fecha: %date% %time%
echo.

REM 1. Actualizar del remoto
git pull origin main --rebase

REM 2. Agregar cambios
git add .

REM 3. Hacer commit
git commit -m "Auto-sync: %date% %time%" || echo "Sin cambios"

REM 4. Subir cambios
git push origin main

echo.
echo Sincronización completada
echo.