#!/bin/bash

# 색상 코드 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Sunguard API 배포 시작 ===${NC}"

# 1단계: Gradle 빌드
echo -e "${YELLOW}1. Gradle 빌드 중...${NC}"
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 빌드 성공${NC}"
else
    echo -e "${RED}✗ 빌드 실패${NC}"
    exit 1
fi

# 2단계: JAR 파일 존재 확인
JAR_FILE="build/libs/sunguard-api-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}✗ JAR 파일을 찾을 수 없습니다: $JAR_FILE${NC}"
    exit 1
fi

echo -e "${GREEN}✓ JAR 파일 확인됨: $JAR_FILE${NC}"

# 3단계: EC2로 파일 전송
echo -e "${YELLOW}2. EC2 서버로 파일 전송 중...${NC}"
scp -i "/Volumes/P31 M.2 NVMe 2TB/문서/Spring/sunguard-aws/keypair/sunguard-keypair.pem" "$JAR_FILE" ec2-user@3.36.247.104:/home/ec2-user/

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 파일 전송 성공${NC}"
else
    echo -e "${RED}✗ 파일 전송 실패${NC}"
    exit 1
fi

echo -e "${GREEN}=== 배포 완료 ===${NC}"
echo -e "${YELLOW}EC2 서버에서 다음 명령어로 실행하세요:${NC}"
echo "ssh -i \"/Volumes/P31 M.2 NVMe 2TB/문서/Spring/sunguard-aws/keypair/sunguard-keypair.pem\" ec2-user@3.36.247.104"
echo "java -jar sunguard-api-0.0.1-SNAPSHOT.jar"
echo "./start.sh"
