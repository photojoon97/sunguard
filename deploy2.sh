echo "=== Sunguard API 배포 시작 ==="

# 1단계: Gradle 빌드
echo "1. Gradle 빌드 중"
./gradlew clean build -x test
    
if [ $? -eq 0 ]; then
    echo "빌드 성공"
else
    echo "빌드 실패"
    exit 1
fi

# 2단계: JAR 파일 존재 확인 
JAR_FILE="build/libs/sunguard-api-0.0.1-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR 파일을 찾을 수 없습니다: $JAR_FILE"
    exit 1
fi

echo "JAR 파일 확인됨: $JAR_FILE"

# 3단계: EC2로 파일 전송
echo "2. EC2 서버로 파일 전송 중"
scp -i "/Volumes/P31 M.2 NVMe 2TB/문서/Spring/sunguard-aws/keypair/sunguard-keypair.pem" "$JAR_FILE" ec2-user@3.36.247.104:/home/ec2-user/

if [ $? -eq 0 ]; then
    echo "파일 전송 성공"
else
    echo "파일 전송 실패"
    exit 1
fi

echo -e "=== 배포 완료 ==="

echo -e "EC2 서버에서 다음 명령어로 실행하세요:"
echo "java -jar sunguard-api-0.0.1-SNAPSHOT.jar"
echo "./start.sh"

