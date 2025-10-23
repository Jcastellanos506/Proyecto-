@echo off
setlocal EnableExtensions EnableDelayedExpansion
title Score APP - One-Click Runner (portable JRE + portable Maven)

rem === 0) Ir a la carpeta del script ===
cd /d "%~dp0"

echo ===============================================
echo   Score APP - One-Click Runner
echo   * Descarga JRE 17 portable si no existe
echo   * Descarga Maven portable si no existe
echo   * Compila y abre la GUI
echo ===============================================
echo.

rem -----------------------------------------------
rem 1) JRE 17 PORTABLE (Temurin) 64-bit
rem -----------------------------------------------
set "JRE_DIR=portable-jre"
set "JRE_BIN=%JRE_DIR%\bin\java.exe"
set "JRE_URL=https://github.com/adoptium/temurin17-binaries/releases/latest/download/OpenJDK17U-jre_x64_windows_hotspot_17.0.12_7.zip"

if not exist "%JRE_BIN%" (
  echo [INFO] JRE 17 portable no encontrado. Descargando...
  set "TMP_ZIP=%TEMP%\jre17.zip"
  powershell -NoLogo -NoProfile -Command ^
    "try { Invoke-WebRequest -Uri '%JRE_URL%' -OutFile '%TMP_ZIP%' -UseBasicParsing } catch { exit 1 }"
  if errorlevel 1 (
    echo [ERROR] No se pudo descargar el JRE. Verifica tu conexion a Internet.
    pause & exit /b 1
  )
  echo [INFO] Extrayendo JRE...
  if exist "%JRE_DIR%" rmdir /s /q "%JRE_DIR%"
  mkdir "%JRE_DIR%"
  powershell -NoLogo -NoProfile -Command ^
    "Expand-Archive -LiteralPath '%TMP_ZIP%' -DestinationPath '%JRE_DIR%' -Force"
  del /f /q "%TMP_ZIP%" >nul 2>&1
  rem mover contenido si viene dentro de una carpeta
  for /d %%D in ("%JRE_DIR%\*") do (
    if exist "%%D\bin\java.exe" (
      xcopy /e /i /y "%%D\*" "%JRE_DIR%\" >nul
      rmdir /s /q "%%D"
    )
  )
)

if not exist "%JRE_BIN%" (
  echo [ERROR] JRE 17 portable no disponible. Abortando.
  pause & exit /b 1
)

echo [OK] JRE listo: %JRE_BIN%
"%JRE_BIN%" -version
echo.

rem -----------------------------------------------
rem 2) MAVEN PORTABLE
rem -----------------------------------------------
set "MVN_DIR=portable-maven"
set "MVN_BIN=%MVN_DIR%\bin\mvn.cmd"
set "MVN_URL=https://downloads.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"

if not exist "%MVN_BIN%" (
  echo [INFO] Maven portable no encontrado. Descargando...
  set "TMP_MVN=%TEMP%\maven.zip"
  powershell -NoLogo -NoProfile -Command ^
    "try { Invoke-WebRequest -Uri '%MVN_URL%' -OutFile '%TMP_MVN%' -UseBasicParsing } catch { exit 1 }"
  if errorlevel 1 (
    echo [ERROR] No se pudo descargar Maven. Verifica tu conexion.
    pause & exit /b 1
  )
  echo [INFO] Extrayendo Maven...
  if exist "%MVN_DIR%" rmdir /s /q "%MVN_DIR%"
  mkdir "%MVN_DIR%"
  powershell -NoLogo -NoProfile -Command ^
    "Expand-Archive -LiteralPath '%TMP_MVN%' -DestinationPath '%MVN_DIR%' -Force"
  del /f /q "%TMP_MVN%" >nul 2>&1
  rem mover contenido si viene en subcarpeta
  for /d %%D in ("%MVN_DIR%\*") do (
    if exist "%%D\bin\mvn.cmd" (
      xcopy /e /i /y "%%D\*" "%MVN_DIR%\" >nul
      rmdir /s /q "%%D"
    )
  )
)

if not exist "%MVN_BIN%" (
  echo [ERROR] Maven portable no disponible. Abortando.
  pause & exit /b 1
)

echo [OK] Maven listo: %MVN_BIN%
call "%MVN_BIN%" -version
echo.

rem -----------------------------------------------
rem 3) COMPILAR Y EMPAQUETAR (somos tolerantes)
rem -----------------------------------------------
set "JAVA_HOME=%CD%\%JRE_DIR%"
set "PATH=%JAVA_HOME%\bin;%MVN_DIR%\bin;%PATH%"

echo [INFO] Limpiando y compilando (skip tests)...
call "%MVN_BIN%" -DskipTests -Dmaven.test.skip=true clean package
if errorlevel 1 (
  echo [WARN] Compilacion fallo. Intentaremos ejecutar por classpath si hay clases compiladas.
)

set "JAR=target\score-app-1.0.0-shaded.jar"
if not exist "%JAR%" (
  set "JAR=target\score-app-1.0.0.jar"
)

rem -----------------------------------------------
rem 4) EJECUTAR GUI
rem -----------------------------------------------
if exist "%JAR%" (
  echo [INFO] Lanzando GUI por JAR: %JAR%
  "%JRE_BIN%" -jar "%JAR%"
  goto :END
)

if exist "target\classes" (
  echo [INFO] Lanzando GUI por classpath (sin JAR)...
  "%JRE_BIN%" -cp "target\classes" com.scoreapp.ui.MainSwing
  goto :END
)

echo [ERROR] No hay JAR ni clases compiladas. Revisa errores de Maven arriba.

:END
echo.
echo [FIN] Cierra esta ventana o presiona una tecla.
pause >nul
endlocal
