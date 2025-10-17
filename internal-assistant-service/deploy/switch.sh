#!/bin/bash
set -e

# 🔹 Bastion 서버 정보
BASTION_USER="ec2-user"
BASTION_HOST="Bastion_PUBLIC_IP"       # 실제 퍼블릭 IP
PEM_KEY="/home/ec2-user/jojeonghyeon-ec2-key1.pem"
INTERNAL_INC="/home/ec2-user/includes/internal_port.inc"
BLUE_PORT=8081
GREEN_PORT=8082

# 🔹 Bastion 서버에서 실행될 명령
read -r -d '' BASTION_CMD <<'EOF'
CURRENT_PORT=$(grep -oP '[0-9]+' /home/ec2-user/includes/internal_port.inc)
if [ "$CURRENT_PORT" == "8081" ]; then
    NEW_PORT=8082
else
    NEW_PORT=8081
fi

echo "[INFO] Switching internal_port to $NEW_PORT"

# internal_port.inc 업데이트
echo "set \$internal_port $NEW_PORT;" | sudo tee /home/ec2-user/includes/internal_port.inc

# Nginx 문법 검사 및 reload
sudo nginx -t && sudo systemctl reload nginx
echo "[SUCCESS] Nginx reloaded with new internal_port $NEW_PORT"
EOF

echo "[INFO] Connecting to Bastion server to switch Nginx port..."
ssh -i "$PEM_KEY" -o StrictHostKeyChecking=no "$BASTION_USER@$BASTION_HOST" "$BASTION_CMD"

echo "[SUCCESS] Nginx port switch complete."
