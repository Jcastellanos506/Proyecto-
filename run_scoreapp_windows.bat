@echo off
setlocal enabledelayedexpansion
title Score APP - Build & Run (Windows)
cd /d "%~dp0"
echo =============================================
echo   Score APP - Build and Run (Windows)
echo   Requires: Java 17+ and Maven 3.9+
echo =============================================
echo.
where java >nul 2>&1
if errorlevel 1 ( echo [ERROR] Java no esta instalado o no esta en el PATH. & goto :END )
where mvn >nul 2>&1
if errorlevel 1 ( echo [ERROR] Maven no esta instalado o no esta en el PATH. & goto :END )
echo [INFO] Versiones detectadas:
java -version
mvn -version
echo.
echo [INFO] Compilando y empacando con Maven...
call mvn clean package
if errorlevel 1 ( echo [ERROR] La compilacion fallo. Revisa los mensajes anteriores. & goto :END )
set JAR=target\score-app-1.0.0.jar
if not exist "%JAR%" ( echo [ERROR] No se encontro el archivo %JAR% despues del build. & goto :END )
echo.
echo [INFO] Ejecutando la aplicacion GUI...
call java -jar "%JAR%"
:END
echo.
echo [FIN] Proceso terminado. Presiona una tecla para salir.
pause >nul
endlocal
