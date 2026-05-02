@echo off
setlocal

set ROOT_DIR=%~dp0..
set JAR_FILE=%ROOT_DIR%\auction-backend\target\auction-backend-0.0.1-SNAPSHOT.jar
set RUN_DIR=%ROOT_DIR%\run
if not exist "%RUN_DIR%" mkdir "%RUN_DIR%"

if not exist "%JAR_FILE%" (
  echo [ERROR] Jar not found: %JAR_FILE%
  echo [INFO] Please run: cd auction-backend ^&^& mvn clean package -DskipTests
  exit /b 1
)

if not exist "%ROOT_DIR%\auction-backend\src\main\resources\application-prod.yml" (
  echo [WARN] application-prod.yml not found.
  echo [WARN] Please copy from application-prod.yml.template and fill values.
)

echo [INFO] Starting backend with prod profile...
start "auction-backend-prod" cmd /c "java -jar \"%JAR_FILE%\" --spring.profiles.active=prod > \"%RUN_DIR%\backend-prod.log\" 2>&1"
echo [INFO] Started. Check log: %RUN_DIR%\backend-prod.log

endlocal
