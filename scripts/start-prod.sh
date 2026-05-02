#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/auction-backend"
JAR_FILE="$BACKEND_DIR/target/auction-backend-0.0.1-SNAPSHOT.jar"
RUN_DIR="$ROOT_DIR/run"
mkdir -p "$RUN_DIR"

if [[ ! -f "$JAR_FILE" ]]; then
  echo "[ERROR] Jar not found: $JAR_FILE"
  echo "[INFO] Run: cd auction-backend && mvn clean package -DskipTests"
  exit 1
fi

if [[ ! -f "$BACKEND_DIR/src/main/resources/application-prod.yml" ]]; then
  echo "[WARN] application-prod.yml not found."
  echo "[WARN] Please copy from application-prod.yml.template and fill values."
fi

echo "[INFO] Starting backend in prod profile..."
nohup java -jar "$JAR_FILE" --spring.profiles.active=prod >"$RUN_DIR/backend-prod.log" 2>&1 &
echo $! >"$RUN_DIR/backend-prod.pid"
echo "[INFO] backend pid: $(cat "$RUN_DIR/backend-prod.pid")"
