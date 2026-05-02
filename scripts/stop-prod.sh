#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
PID_FILE="$ROOT_DIR/run/backend-prod.pid"

if [[ ! -f "$PID_FILE" ]]; then
  echo "[INFO] PID file not found: $PID_FILE"
  exit 0
fi

PID="$(cat "$PID_FILE")"
if ps -p "$PID" >/dev/null 2>&1; then
  kill "$PID"
  echo "[INFO] Stopped backend process: $PID"
else
  echo "[INFO] Process not running: $PID"
fi

rm -f "$PID_FILE"
