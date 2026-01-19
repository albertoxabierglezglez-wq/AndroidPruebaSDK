@echo off
chcp 65001 >nul
echo ========================================
echo COMPILADOR ANDROID OFFLINE - WALLET APP
echo ========================================
echo.

set JAVA_HOME=C:\Program Files (x86)\Java\jdk-1.8
set ANDROID_HOME=C:\Android\SDK
set PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\build-tools\29.0.3;%PATH%

echo [1/7] Limpiando...
rmdir /s /q bin gen obj 2>nul
mkdir bin gen obj 2>nul

echo [2/7] Compilando recursos...
aapt package -f -m -J gen -S res -M AndroidManifest.xml -I "%ANDROID_HOME%\platforms\android-29\android.jar" --custom-package com.axgg.walletmultired
if errorlevel 1 goto error

echo [3/7] Compilando Java...
javac -d obj -cp "%ANDROID_HOME%\platforms\android-29\android.jar" -sourcepath src;gen src\com\axgg\walletmultired\*.java gen\com\axgg\walletmultired\R.java
if errorlevel 1 goto error

echo [4/7] Creando DEX...
d8 --lib "%ANDROID_HOME%\platforms\android-29\android.jar" --min-api 25 --output bin obj\com\axgg\walletmultired\*.class
if errorlevel 1 goto error

echo [5/7] Creando APK...
aapt package -f -M AndroidManifest.xml -S res -I "%ANDROID_HOME%\platforms\android-29\android.jar" -F bin\app-unsigned.apk
if errorlevel 1 goto error

echo [6/7] Agregando DEX...
cd bin && aapt add app-unsigned.apk classes.dex && cd ..
if errorlevel 1 goto error

echo [7/7] Firmando...
if not exist debug.keystore (
    echo Creando debug.keystore (responde TODO con: android, luego: yes)...
    echo android | keytool -genkey -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=android, OU=android, O=android, L=android, ST=android, C=US"
)
apksigner sign --ks debug.keystore --ks-pass pass:android --key-pass pass:android --out bin\wallet-app.apk bin\app-unsigned.apk
if errorlevel 1 goto error

echo.
echo ========================================
echo ¡COMPILACION EXITOSA! 🎉
echo ========================================
echo APK: bin\wallet-app.apk (^>42KB)
echo.
echo Para instalar: adb install bin\wallet-app.apk
echo Para reinstalar: adb install -r bin\wallet-app.apk
echo ========================================
dir bin\wallet-app.apk | findstr "wallet-app"
pause
exit /b 0

:error
echo.
echo ERROR en la compilacion.
pause
exit /b 1