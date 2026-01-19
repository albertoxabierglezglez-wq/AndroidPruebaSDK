@echo off
echo Reemplazando CardView por LinearLayout...
powershell -Command "(Get-Content 'res\layout\activity_main.xml' -Raw) -replace 'androidx\.cardview\.widget\.CardView', 'LinearLayout' | Set-Content 'res\layout\activity_main.xml' -Encoding UTF8"
echo Listo. Se removieron las referencias a CardView.
pause