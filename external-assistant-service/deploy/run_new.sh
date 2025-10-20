#!/bin/bash
set -e

APP_NAME="external-assistant-service"
APP_DIR="/home/ec2-user/sol-assistant-backend"
TAR_FILE="$APP_DIR/external-assistant-service.tar"
BLUE_PORT=8081
GREEN_PORT=8082
PORT_FILE="/home/ec2-user/external_port.txt"

BASTION_USER="ec2-user"
BASTION_HOST="43.203.242.57"
PEM_KEY="/home/ec2-user/jojeonghyeon-ec2-key1.pem"
EXTERNAL_INC="/home/ec2-user/includes/external_port.inc"

echo "[INFO] === Checking Active Port on Bastion ==="
CURRENT_PORT=$(ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no \
    "$BASTION_USER@$BASTION_HOST" "grep -oP '[0-9]+' $EXTERNAL_INC")
echo "[INFO] Current Nginx external_port: $CURRENT_PORT"

if [ "$CURRENT_PORT" == "$BLUE_PORT" ]; then
    IDLE_PORT=$GREEN_PORT
else
    IDLE_PORT=$BLUE_PORT
fi
echo "[INFO] Deploying new container to Idle Port: $IDLE_PORT"

echo "$IDLE_PORT" > $PORT_FILE

EXISTING_CONTAINER=$(docker ps -a -q -f "publish=$IDLE_PORT")
if [ -n "$EXISTING_CONTAINER" ]; then
    echo "[INFO] Removing old container on port $IDLE_PORT..."
    docker rm -f $EXISTING_CONTAINER || true
fi

CONFLICT_CONTAINER=$(docker ps -a -q -f "name=${APP_NAME}_${IDLE_PORT}")
if [ -n "$CONFLICT_CONTAINER" ]; then
    echo "[WARN] Removing conflicting container name: ${APP_NAME}_${IDLE_PORT}"
    docker rm -f $CONFLICT_CONTAINER || true
fi

echo "[INFO] Loading Docker image..."
docker load -i $TAR_FILE

docker image prune -f -f > /dev/null 2>&1 || true

echo "[INFO] Running new container..."
docker run -d --name ${APP_NAME}_${IDLE_PORT} -p $IDLE_PORT:8081 $APP_NAME:latest

echo "[INFO] ✅ New container running on port $IDLE_PORT"
