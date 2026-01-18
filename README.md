# MiApp Android - Build Offline

Proyecto Android que compila **100% offline** usando solo:
- SDK Android en `C:\Android\SDK`
- JARs de `tools\lib\`

## Requisitos
- Android SDK instalado en `C:\Android\SDK`
- Java JDK 17+
- Gradle 8.5

## Build
```bash
gradle assembleDebug --offline --no-daemon