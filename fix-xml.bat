@echo off
echo Corrigiendo archivos XML...
chcp 65001 >nul

:: Para cada archivo XML, elimina caracteres antes de <?xml
for /r %%f in (*.xml) do (
    echo Procesando: %%~nxf
    powershell -Command "(Get-Content '%%f' -Raw) -replace '^[^<]*<\?xml', '<?xml' | Set-Content '%%f' -Encoding UTF8"
)

echo.
echo ¡Todos los XML han sido corregidos!
pause