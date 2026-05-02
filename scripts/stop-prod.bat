@echo off
setlocal

for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080 ^| findstr LISTENING') do (
  echo [INFO] Stopping PID %%a on port 8080
  taskkill /PID %%a /F >nul 2>nul
)

echo [INFO] Stop command executed.
endlocal
