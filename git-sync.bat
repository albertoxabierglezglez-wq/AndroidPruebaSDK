@echo off
title Sincronizador Git - %cd%
color 0A

echo ========================================
echo        SINCRONIZADOR GIT
echo ========================================
echo.

:menu
echo [1] Estado de Git
echo [2] Agregar todos los archivos
echo [3] Hacer Commit
echo [4] Push a remoto
echo [5] Pull desde remoto
echo [6] Ver historial
echo [7] Configurar usuario
echo [8] Salir
echo.

set /p opcion="Selecciona opcion (1-8): "

if "%opcion%"=="1" goto status
if "%opcion%"=="2" goto add
if "%opcion%"=="3" goto commit
if "%opcion%"=="4" goto push
if "%opcion%"=="5" goto pull
if "%opcion%"=="6" goto log
if "%opcion%"=="7" goto config
if "%opcion%"=="8" exit

:status
git status
goto menu

:add
set /p mensaje="Mensaje para commit: "
git add .
git commit -m "%mensaje%"
goto menu

:commit
set /p mensaje="Mensaje del commit: "
git commit -m "%mensaje%"
goto menu

:push
git push origin main
echo.
echo ✅ Push completado
goto menu

:pull
git pull origin main
echo.
echo ✅ Pull completado
goto menu

:log
git log --oneline --graph --all
goto menu

:config
set /p nombre="Tu nombre: "
set /p email="Tu email: "
git config --global user.name "%nombre%"
git config --global user.email "%email%"
echo ✅ Configuracion guardada
goto menu