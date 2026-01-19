 @echo off
chcp 65001 >nul
echo ========================================
echo COMPILADOR ANDROID OFFLINE - WALLET APP
echo ========================================
echo.

REM 1. Configurar variables de entorno
set JAVA_HOME=C:\Program Files (x86)\Java\jdk-1.8
set ANDROID_HOME=C:\Android\SDK
set PATH=%JAVA_HOME%\bin;%ANDROID_HOME%\build-tools\29.0.3;%ANDROID_HOME%\platform-tools;%PATH%

echo [1/7] Configurando entorno...
echo Java: %JAVA_HOME%
echo Android SDK: %ANDROID_HOME%
echo.

REM 2. Crear estructura de directorios
echo [2/7] Creando estructura de directorios...
if not exist "bin" mkdir bin
if not exist "gen" mkdir gen
if not exist "res\layout" mkdir res\layout
if not exist "res\values" mkdir res\values
if not exist "libs" mkdir libs
echo.

REM 3. Compilar recursos
echo [3/7] Compilando recursos...
aapt package -f -m -J gen -S res -M AndroidManifest.xml -I "%ANDROID_HOME%\platforms\android-29\android.jar"
if errorlevel 1 (
    echo ERROR compilando recursos
    pause
    exit /b 1
)
echo.

REM 4. Compilar Java
echo [4/7] Compilando codigo Java...
javac -d bin -cp "%ANDROID_HOME%\platforms\android-29\android.jar" -sourcepath src;gen src\com\axgg\walletmultired\*.java gen\com\axgg\walletmultired\R.java
if errorlevel 1 (
    echo ERROR compilando Java
    pause
    exit /b 1
)
echo.

REM 5. Crear DEX
echo [5/7] Creando archivo DEX...
dx --dex --output=bin\classes.dex bin
if errorlevel 1 (
    echo ERROR creando DEX
    pause
    exit /b 1
)
echo.

REM 6. Crear APK sin firmar
echo [6/7] Creando APK sin firmar...
aapt package -f -M AndroidManifest.xml -S res -I "%ANDROID_HOME%\platforms\android-29\android.jar" -F bin\app-unsigned.apk
if errorlevel 1 (
    echo ERROR creando APK
    pause
    exit /b 1
)

REM Agregar DEX al APK
cd bin
aapt add app-unsigned.apk classes.dex
cd ..
echo.

REM 7. Firmar APK
echo [7/7] Firmando APK...
echo.
echo INFO: Si no tienes debug.keystore, crealo con:
echo keytool -genkey -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000
echo.

if exist debug.keystore (
    apksigner sign --ks debug.keystore --ks-pass pass:android --key-pass pass:android --out bin\wallet-app.apk bin\app-unsigned.apk
    echo.
    echo ========================================
    echo ¡COMPILACION EXITOSA!
    echo APK generado: bin\wallet-app.apk
    echo ========================================
) else (
    echo ERROR: No se encuentra debug.keystore
    echo Crea el keystore primero con el comando keytool mostrado arriba
)

pause