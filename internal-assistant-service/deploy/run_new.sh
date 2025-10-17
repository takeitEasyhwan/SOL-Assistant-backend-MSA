#!/bin/bash
set -e

APP_NAME="internal-assistant-service"
TAR_FILE="/home/ec2-user/sol-assistant-backend/internal-assistant-service.tar"
PORT=8081

echo "[INFO] Stopping existing container..."
docker stop $APP_NAME || true
docker rm $APP_NAME || true

echo "[INFO] Loading Docker image from tar..."
docker load -i $TAR_FILE

echo "[INFO] Running new container on port $PORT..."
docker run -d --name $APP_NAME -p $PORT:$PORT $APP_NAME:latest
