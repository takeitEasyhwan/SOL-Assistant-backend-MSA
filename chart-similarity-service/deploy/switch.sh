#!/bin/bash
set -ex   # -e: 에러 시 종료, -x: 디버그 출력

BASTION_USER="ec2-user"
BASTION_HOST="43.203.242.57"
PEM_KEY="/home/ec2-user/jojeonghyeon-ec2-key1.pem"

ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no -v "$BASTION_USER@$BASTION_HOST" 'bash -s' <<'ENDSSH'
set -ex

INSIGHT_INC="/home/ec2-user/includes/insight_port.inc"

CURRENT_PORT=$(grep -o "[0-9][0-9]*" "$INSIGHT_INC")
echo "[DEBUG] Current insight_port: $CURRENT_PORT"

if [ "$CURRENT_PORT" = "8081" ]; then
    NEW_PORT=8082
else
    NEW_PORT=8081
fi
echo "[INFO] Switching insight_port to $NEW_PORT"

echo "set \$insight_port $NEW_PORT;" | sudo tee "$INSIGHT_INC"

# Nginx 문법 체크
sudo nginx -t

# 문법 통과 시 reload
sudo systemctl reload nginx
echo "[SUCCESS] Nginx reloaded with new insight_port $NEW_PORT"
ENDSSH
