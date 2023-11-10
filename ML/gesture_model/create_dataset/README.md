# Release Note

## v1

- 수어 영상(`.mp4`) 및 형태소 파일(`.txt`) 위치 : `../data/`
- MediaPipe로 hand landmark, pose landmark 추출(image 기반)
- 추출된 landmark를 이용해 각도 계산
- 왼손만 2개 또는 오른손만 2개 나오는 경우 예외 처리
- hand landmark의 score가 0.8 이상인 경우만 활용

## v2

- Mediapipe로 추출한 데이터가 비어있는 경우 저장 안 함
- label 데이터 저장

## v3

- 리팩토링
  - 함수화
  - input을 받아 file root 및 model path 설정

## v4

- Mediapipe로 인식된 frame이 10개 이상일 경우 저장

## v5

- MediaPipe landmark 추출 방식을 video 기반으로 변경

## v5_1

- actions를 dictionary 형태로 변경
  - 하나의 형태소에 대해 여러 수어 영상이 있는 경우 처리
- 형태소 2가지 → 석사, 연구
- video 이름에서 형태소 추출

## v6

- lag를 5로 설정

## v7

- video 이름에서 형태소 추출
- 하나의 형태소에 대해 여러 수어 영상이 올 수 있도록 형태소(actions)를 dictionary로 변경
- MediaPipe 설정 한 번만 하도록 수정
- video capture를 1개만 사용하도록 변경
- input을 받아 dataset type 설정 가능(train / test, default: train)

## v8

- 빈 문자열도 가능하도록 action 추출 로직 수정
- input을 받아 MediaPipe pose model bundle 설정 가능(lite / full / heavy, default: heavy)

