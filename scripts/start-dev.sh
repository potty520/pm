#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/auction-backend"
FRONTEND_DIR="$ROOT_DIR/auction-frontend"
RUN_DIR="$ROOT_DIR/run"
mkdir -p "$RUN_DIR"

echo "[INFO] Root: $ROOT_DIR"

echo "[INFO] Starting backend..."
nohup bash -lc "cd '$BACKEND_DIR' && mvn spring-boot:run" >"$RUN_DIR/backend-dev.log" 2>&1 &
echo $! >"$RUN_DIR/backend-dev.pid"

echo "[INFO] Starting frontend..."
nohup bash -lc "cd '$FRONTEND_DIR' && npm run dev -- --host 0.0.0.0" >"$RUN_DIR/frontend-dev.log" 2>&1 &
echo $! >"$RUN_DIR/frontend-dev.pid"

echo "[INFO] Dev services started."
echo "[INFO] Frontend: http://localhost:5173"
echo "[INFO] Backend : http://localhost:8080"
