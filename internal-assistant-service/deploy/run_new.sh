#!/bin/bash
set -e

APP_NAME="internal-assistant-service"
IMAGE_NAME="internal-assistant-service:latest"
PORT=8081

echo "[INFO] Stopping existing container..."
docker stop $APP_NAME || true
docker rm $APP_NAME || true
docker rmi $IMAGE_NAME || true

echo "[INFO] Building Docker image..."
docker build -t $IMAGE_NAME /home/ec2-user/sol-assistant-backend/

echo "[INFO] Running new container on port $PORT..."
docker run -d --name $APP_NAME -p $PORT:$PORT $IMAGE_NAME
