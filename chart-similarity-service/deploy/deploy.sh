#!/bin/bash
set -e

APP_NAME="insight-assistant-service"
TAR_FILE="/home/ec2-user/sol-assistant-backend/insight-assistant-service.tar"
PORT=8081

echo "[INFO] Starting deployment for $APP_NAME on port $PORT..."

# 1. 포트를 사용하는 모든 컨테이너 제거
EXISTING_CONTAINER=$(docker ps -q -f "publish=$PORT")
if [ -n "$EXISTING_CONTAINER" ]; then
  echo "[INFO] Stopping and removing container(s) using port $PORT..."
  docker rm -f $EXISTING_CONTAINER
fi

# 2. 기존 이름으로 실행 중인 컨테이너 제거
EXISTING_CONTAINER_BY_NAME=$(docker ps -q -f "name=$APP_NAME")
if [ -n "$EXISTING_CONTAINER_BY_NAME" ]; then
  echo "[INFO] Stopping and removing container named $APP_NAME..."
  docker rm -f $APP_NAME
fi

# 3. 포트를 사용하는 프로세스가 있으면 종료
PORT_IN_USE=$(lsof -ti tcp:$PORT || true)
if [ -n "$PORT_IN_USE" ]; then
  echo "[INFO] Killing process using port $PORT..."
  kill -9 $PORT_IN_USE
fi

# 4. Docker 이미지 로드
echo "[INFO] Loading Docker image from tar..."
docker load -i $TAR_FILE

# 5. dangling 이미지 정리
echo "[INFO] Cleaning up dangling Docker images..."
docker image prune -f

# 6. 새로운 컨테이너 실행
echo "[INFO] Running new container on port $PORT..."
docker run -d --name $APP_NAME -p $PORT:$PORT $APP_NAME:latest

echo "[INFO] Deployment complete! Running containers:"
docker ps | grep $APP_NAME
