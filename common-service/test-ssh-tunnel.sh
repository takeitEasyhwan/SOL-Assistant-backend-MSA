#!/bin/bash

# SSH 터널링 테스트 스크립트
echo "SSH 터널링 테스트 시작..."

# SSH 키 파일 권한 확인
SSH_KEY="./src/main/resources/ssh/ssh.pem"
if [ -f "$SSH_KEY" ]; then
    echo "SSH 키 파일 존재: $SSH_KEY"
    ls -la "$SSH_KEY"
else
    echo "SSH 키 파일을 찾을 수 없습니다: $SSH_KEY"
    exit 1
fi

# SSH 연결 테스트
echo "SSH 연결 테스트 중..."
ssh -i "$SSH_KEY" -o ConnectTimeout=10 -o StrictHostKeyChecking=no ec2-user@3.35.238.207 "echo 'SSH 연결 성공!'"

if [ $? -eq 0 ]; then
    echo "SSH 연결 테스트 성공!"
else
    echo "SSH 연결 테스트 실패!"
    exit 1
fi

# SSH 터널 생성 테스트
echo "SSH 터널 생성 테스트 중..."
ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no -L 13306:sinhan-assistant-rds.c5gkus4mo5za.ap-northeast-2.rds.amazonaws.com:3306 -N -f ec2-user@3.35.238.207

if [ $? -eq 0 ]; then
    echo "SSH 터널 생성 성공!"
    echo "터널 포트: 13306"
    echo "터널을 종료하려면: pkill -f 'ssh.*13306'"
else
    echo "SSH 터널 생성 실패!"
    exit 1
fi

echo "테스트 완료!"
