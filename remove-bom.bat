@echo off
echo Eliminando BOM (caracteres invisibles) de MainActivity.java...
powershell -Command "
$content = Get-Content 'src\com\axgg\walletmultired\MainActivity.java' -Encoding UTF8 -Raw
# Eliminar el BOM (EF BB BF en hexadecimal)
$content = $content -replace '^\xEF\xBB\xBF', ''
# También eliminar cualquier otro caracter no-ASCII al inicio
$content = $content -replace '^[^\x00-\x7F]+', ''
Set-Content 'src\com\axgg\walletmultired\MainActivity.java' -Value $content -Encoding UTF8
"
echo ¡BOM eliminado! Ahora compila de nuevo.
pause