#!/bin/bash
set -e

APP_NAME="insight-assistant-service"
APP_DIR="/home/ec2-user/sol-assistant-backend"
TAR_FILE="$APP_DIR/${APP_NAME}.tar"
BLUE_PORT=8081
GREEN_PORT=8082
PORT_FILE="/home/ec2-user/insight_port.txt"

BASTION_USER="ec2-user"
BASTION_HOST="43.203.242.57"
PEM_KEY="/home/ec2-user/jojeonghyeon-ec2-key1.pem"
INSIGHT_INC="/home/ec2-user/includes/insight_port.inc"

echo "[INFO] === Checking Active Port on Bastion ==="
CURRENT_PORT=$(ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no \
    "$BASTION_USER@$BASTION_HOST" "grep -oP '[0-9]+' $INSIGHT_INC")
echo "[INFO] Current Nginx insight_port: $CURRENT_PORT"

# 🔹 Idle 포트 결정
IDLE_PORT=$([[ "$CURRENT_PORT" == "$BLUE_PORT" ]] && echo "$GREEN_PORT" || echo "$BLUE_PORT")
echo "[INFO] Deploying new container to Idle Port: $IDLE_PORT"
echo "$IDLE_PORT" > "$PORT_FILE"

# 🔹 1. 기존 컨테이너 제거 (이름 기준)
EXISTING_CONTAINER=$(docker ps -a -q -f "name=${APP_NAME}_${IDLE_PORT}")
if [ -n "$EXISTING_CONTAINER" ]; then
    echo "[INFO] Removing existing container with name: ${APP_NAME}_${IDLE_PORT}"
    docker rm -f "$EXISTING_CONTAINER" || true
fi

# 🔹 2. 포트 충돌 컨테이너 제거 (Optional)
PORT_CONFLICT=$(docker ps -a -q -f "publish=$IDLE_PORT")
if [ -n "$PORT_CONFLICT" ]; then
    echo "[INFO] Removing container using port $IDLE_PORT..."
    docker rm -f "$PORT_CONFLICT" || true
fi

# 🔹 Docker 이미지 체크 및 로드
if [ ! -f "$TAR_FILE" ]; then
    echo "[ERROR] Docker image file not found: $TAR_FILE"
    exit 1
fi

echo "[INFO] Loading Docker image..."
docker load -i "$TAR_FILE"

# 🔹 Dangling 이미지 정리
docker image prune -f > /dev/null 2>&1 || true

# 🔹 새 컨테이너 실행
echo "[INFO] Running new container..."
docker run -d \
  --name "${APP_NAME}_${IDLE_PORT}" \
  -p "$IDLE_PORT":8081 \
  "$APP_NAME:latest"

echo "[INFO] ✅ New container running on port $IDLE_PORT"
