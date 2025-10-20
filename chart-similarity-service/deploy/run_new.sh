#!/bin/bash
set -e

APP_NAME="insight-assistant-service"
APP_DIR="/home/ec2-user/sol-assistant-backend"
TAR_FILE="$APP_DIR/${APP_NAME}.tar"
BLUE_PORT=8081
GREEN_PORT=8082
PORT_FILE="/home/ec2-user/insight_port.txt"

# Bastion 서버 정보
BASTION_USER="ec2-user"
BASTION_HOST="43.203.242.57"
PEM_KEY="/home/ec2-user/jojeonghyeon-ec2-key1.pem"
INSIGHT_INC="/home/ec2-user/includes/insight_port.inc"

echo "[INFO] === Checking Active Port on Bastion ==="
if ! CURRENT_PORT=$(ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no -o ConnectTimeout=5 \
    "$BASTION_USER@$BASTION_HOST" "grep -oP '[0-9]+' $INSIGHT_INC" 2>/dev/null); then
  echo "[ERROR] Failed to connect to Bastion or read $INSIGHT_INC"
  exit 1
fi
echo "[INFO] Current Nginx insight_port: $CURRENT_PORT"

# Idle 포트 결정
if [ "$CURRENT_PORT" == "$BLUE_PORT" ]; then
    IDLE_PORT=$GREEN_PORT
else
    IDLE_PORT=$BLUE_PORT
fi
echo "[INFO] Deploying new container to Idle Port: $IDLE_PORT"
echo "$IDLE_PORT" > "$PORT_FILE"

# 기존 Idle 컨테이너 종료 및 삭제 (모든 상태 포함)
EXISTING_CONTAINER=$(docker ps -a -q -f "publish=$IDLE_PORT")
if [ -n "$EXISTING_CONTAINER" ]; then
    echo "[INFO] Removing old container on port $IDLE_PORT..."
    docker rm -f "$EXISTING_CONTAINER" || true
fi

# 이름 충돌 제거
CONFLICT_CONTAINER=$(docker ps -a -q -f "name=${APP_NAME}_${IDLE_PORT}")
if [ -n "$CONFLICT_CONTAINER" ]; then
    echo "[WARN] Removing conflicting container name: ${APP_NAME}_${IDLE_PORT}"
    docker rm -f "$CONFLICT_CONTAINER" || true
fi

# Docker 이미지 존재 체크
if [ ! -f "$TAR_FILE" ]; then
  echo "[ERROR] Docker image file not found: $TAR_FILE"
  exit 1
fi

# Docker 이미지 로드
echo "[INFO] Loading Docker image..."
docker load -i "$TAR_FILE"

# Dangling 이미지 정리
docker image prune -f > /dev/null 2>&1 || true

# 새 컨테이너 실행
docker run -d \
  --name "${APP_NAME}_${IDLE_PORT}" \
  -p "$IDLE_PORT":8081 \
  "$APP_NAME:latest"

echo "[INFO] ✅ New container running on port $IDLE_PORT"
