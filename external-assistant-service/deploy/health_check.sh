#!/bin/bash
set -e

PORT_FILE="/home/ec2-user/external_port.txt"

# 1. 파일 존재 여부 확인
if [ ! -f "$PORT_FILE" ]; then
  echo "[ERROR] Port file not found: $PORT_FILE"
  exit 1
fi

# 2. 포트 읽기 (공백 제거)
PORT=$(cat $PORT_FILE | tr -d '[:space:]')
if [ -z "$PORT" ]; then
  echo "[ERROR] Port file is empty: $PORT_FILE"
  exit 1
fi

URL="http://127.0.0.1:${PORT}/api/v1/external/health"
MAX_RETRIES=10
SLEEP_SECONDS=5

echo "[INFO] Health check starting on $URL"
echo "[DEBUG] Using PORT=$PORT from $PORT_FILE"

for ((i=1;i<=MAX_RETRIES;i++))
do
  echo "[DEBUG] Attempt #$i..."
  STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || echo "CURL_FAIL")

  if [ "$STATUS" == "CURL_FAIL" ]; then
    echo "[ERROR] curl command failed"
  elif [ "$STATUS" -eq 200 ]; then
    echo "[SUCCESS] Health check passed (HTTP $STATUS)"
    exit 0
  else
    echo "[WAIT] Server not ready ($i/$MAX_RETRIES) - Status: $STATUS"
  fi

  sleep $SLEEP_SECONDS
done

echo "[ERROR] Health check failed after $MAX_RETRIES retries"
exit 1
