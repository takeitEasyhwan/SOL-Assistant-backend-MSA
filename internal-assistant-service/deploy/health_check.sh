#!/bin/bash
set -e

PORT_FILE="/home/ec2-user/internal_port.txt"
PORT=$(cat $PORT_FILE)
URL="http://127.0.0.1:${PORT}/api/v1/internal/health"
MAX_RETRIES=10
SLEEP_SECONDS=5

echo "[INFO] Health check starting on $URL"

for ((i=1;i<=MAX_RETRIES;i++))
do
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" $URL)
  if [ "$STATUS" -eq 200 ]; then
    echo "[SUCCESS] Health check passed (HTTP $STATUS)"
    exit 0
  fi
  echo "[WAIT] Server not ready ($i/$MAX_RETRIES) - Status: $STATUS"
  sleep $SLEEP_SECONDS
done

echo "[ERROR] Health check failed after $MAX_RETRIES retries"
exit 1
