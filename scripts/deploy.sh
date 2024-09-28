#!/bin/bash

# EC2 인스턴스의 홈 디렉토리로 이동
cd /home/ec2-user

# 환경변수 DOCKER_APP_NAME을 bilyeocho으로 설정
DOCKER_APP_NAME=bilyeocho

# 실행 중인 blue가 있는지 확인
EXIST_BLUE=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep Up)

# 배포 시작한 날짜와 시간을 기록
echo "배포 시작 일자 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log

# green이 실행 중이면 blue up
if [ -z "$EXIST_BLUE" ]; then

  echo "blue 배포 시작 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log

  # docker-compose.blue.yml 파일을 사용하여 connectdog-blue 프로젝트의 컨테이너를 빌드하고 실행
  sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d --build

  # 30초 동안 대기
  sleep 30

  # blue가 문제 없이 배포 되었는지 현재 실행여부를 확인한다
  BLUE_HEALTH=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep Up)

  # blue가 현재 실행중이지 않다면 런타임 에러 또는 다른 이유로 배포가 되지 못한 상태
  if [ -z "$BLUE_HEALTH" ]; then
    # 에러 처리 로직 추가 가능
  else
    echo "green 중단 시작 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log

    # docker-compose.green.yml 파일을 사용하여 connectdog-green 프로젝트의 컨테이너를 중지
    sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml down

    # 사용하지 않는 이미지 삭제
    sudo docker image prune -af

    echo "green 중단 완료 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log
  fi

# blue가 실행중이면 green up
else
  echo "green 배포 시작 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log
  sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d --build

  sleep 30
  # green이 문제 없이 배포 되었는지 현재 실행여부를 확인한다
  GREEN_HEALTH=$(sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml ps | grep Up)

  # green이 현재 실행중이지 않다면 런타임 에러 또는 다른 이유로 배포가 되지 못한 상태
  if [ -z "$GREEN_HEALTH" ]; then
      # 에러 처리 로직 추가 가능
  else
      echo "blue 중단 시작 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log

      # docker-compose.blue.yml 파일을 사용하여 connectdog-green 프로젝트의 컨테이너를 중지
      sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml down

      # 사용하지 않는 이미지 삭제
      sudo docker image prune -af

      echo "blue 중단 완료 : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log
  fi
fi

echo "배포 종료  : $(date +%Y)-$(date +%m)-$(date +%d) $(date +%H):$(date +%M):$(date +%S)" >> /home/ec2-user/deploy.log

echo "===================== 배포 완료 =====================" >> /home/ec2-user/deploy.log
echo >> /home/ec2-user/deploy.log
