#!/bin/bash
set -e

APP_NAME="internal-assistant-service"
APP_DIR="/home/ec2-user/sol-assistant-backend"
TAR_FILE="$APP_DIR/internal-assistant-service.tar"
BLUE_PORT=8081
GREEN_PORT=8082

# Bastion 서버 정보
BASTION_USER="ec2-user"
BASTION_HOST="43.203.242.57"        # 실제 퍼블릭 IP 입력
PEM_KEY="/home/ec2-user/jojeonghyeon-ec2-key1.pem"
INTERNAL_INC="/home/ec2-user/includes/internal_port.inc"

echo "[INFO] === Checking Active Port on Bastion ==="

# Bastion에서 현재 Nginx가 바라보는 포트 확인
CURRENT_PORT=$(ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no \
    "$BASTION_USER@$BASTION_HOST" "grep -oP '[0-9]+' $INTERNAL_INC")
echo "[INFO] Current Nginx internal_port: $CURRENT_PORT"

# Idle 포트 결정
if [ "$CURRENT_PORT" == "$BLUE_PORT" ]; then
    IDLE_PORT=$GREEN_PORT
else
    IDLE_PORT=$BLUE_PORT
fi
echo "[INFO] Deploying new container to Idle Port: $IDLE_PORT"

# 기존 Idle 컨테이너 종료
EXISTING_CONTAINER=$(docker ps -q -f "publish=$IDLE_PORT")
if [ -n "$EXISTING_CONTAINER" ]; then
    echo "[INFO] Removing old container on port $IDLE_PORT..."
    docker rm -f $EXISTING_CONTAINER
fi

# Docker 이미지 로드
echo "[INFO] Loading Docker image..."
docker load -i $TAR_FILE

# Dangling 이미지 정리
docker image prune -f -f

# 새 컨테이너 실행
docker run -d --name ${APP_NAME}_${IDLE_PORT} -p $IDLE_PORT:8081 $APP_NAME:latest
echo "[INFO] New container running on port $IDLE_PORT"

echo "[INFO] Next: Run health_check.sh and switch.sh to complete deployment"
