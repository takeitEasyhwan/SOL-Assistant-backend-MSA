#!/bin/bash
set -e

APP_NAME="internal-assistant-service"
TAR_FILE="/home/ec2-user/sol-assistant-backend/internal-assistant-service.tar"
PORT=8081

echo "[INFO] Checking for existing container using port $PORT..."
# 포트를 쓰는 모든 컨테이너 조회
EXISTING_CONTAINER=$(docker ps -q -f "publish=$PORT")

if [ -n "$EXISTING_CONTAINER" ]; then
  echo "[INFO] Stopping and removing container(s) using port $PORT..."
  docker rm -f $EXISTING_CONTAINER
fi

# 기존 이름의 컨테이너가 있다면 제거
EXISTING_CONTAINER_BY_NAME=$(docker ps -q -f "name=$APP_NAME")
if [ -n "$EXISTING_CONTAINER_BY_NAME" ]; then
  echo "[INFO] Stopping and removing container named $APP_NAME..."
  docker rm -f $APP_NAME
fi

echo "[INFO] Loading Docker image from tar..."
docker load -i $TAR_FILE

echo "[INFO] Checking if port $PORT is in use by any process..."
PORT_IN_USE=$(lsof -ti tcp:$PORT || true)
if [ -n "$PORT_IN_USE" ]; then
  echo "[INFO] Killing process using port $PORT..."
  kill -9 $PORT_IN_USE
fi

echo "[INFO] Running new container on port $PORT..."
docker run -d --name $APP_NAME -p $PORT:$PORT $APP_NAME:latest

echo "[INFO] Deployment complete!"
docker ps | grep $APP_NAME
