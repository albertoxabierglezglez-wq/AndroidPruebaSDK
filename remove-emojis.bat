@echo off
echo Eliminando emojis y caracteres especiales de MainActivity.java...
powershell -Command "(Get-Content 'src\com\axgg\walletmultired\MainActivity.java' -Encoding UTF8) -replace '[^\x00-\x7F]', '' | Set-Content 'src\com\axgg\walletmultired\MainActivity.java' -Encoding UTF8"
echo ¡Emojis eliminados! Ahora compila de nuevo con: compile.bat
pause