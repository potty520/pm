@echo off
setlocal

set ROOT_DIR=%~dp0..
set BACKEND_DIR=%ROOT_DIR%\auction-backend
set FRONTEND_DIR=%ROOT_DIR%\auction-frontend

echo [INFO] Root: %ROOT_DIR%
echo [INFO] Starting backend...
start "auction-backend-dev" cmd /k "cd /d %BACKEND_DIR% && mvn spring-boot:run"

echo [INFO] Starting frontend...
start "auction-frontend-dev" cmd /k "cd /d %FRONTEND_DIR% && npm run dev"

echo [INFO] Dev services started.
echo [INFO] Frontend: http://localhost:5173
echo [INFO] Backend : http://localhost:8080

endlocal
