# SONTOK
![header](./assets/sontok_logo.png){width=30%}

🎈 프로젝트명 : SONTOK

📌 프로젝트 컨셉 : 실시간 수어 번역 애플리케이션

🛠 개발 기간 : 23.10.09 ~ 23.11.17 (6주)

## **1️⃣ 주요기능**

- ***농인**과 ***청인**의 실시간 대화를 도와주는 애플리케이션
- 대화의 전체 내용을 저장 및 열람하는 기능

***농인**: 청각장애인 중 한국수어를 제1언어로 사용하고 한국수어에 기반한 독특한 농문화를 영위하며 사는 사람\
***청인**: 농인에 대비되는 개념으로 음성언어를 중심으로 의사소통을 하는 사람

## **2️⃣ 세부기능**
|구분|기능|설명|비고|
|:---|:---|:---|:---|
|1|실시간 수어 번역|농인의 수어를 텍스트로 번역, 텍스트를 음성으로 변환하여 청인에게 전달||
|2|음성을 텍스트로의 변환|청인의 음성을 텍스트로 변환하여 농인에게 전달하는 기능||
|3|대화 내용 저장|실시간 대화 내용 전체를 저장|수어, 음성 대화를 일괄 텍스트화하여 저장|
|4|대화 내용 열람|저장한 대화 내용을 불러오기||

## **3️⃣ 아키텍처**
### 시스템 다이어그램
![systemdiagram1](./assets/시스템다이어그램1)
![systemdiagram1](./assets/시스템다이어그램2)

### 개발환경
<h4>🌐 공통</h4>

| 상세       |       내용        |
| ---------- | :--------------- |
| GitLab     | 형상 관리 |
| Jira       | 일정 및 이슈 관리 |
| Mattermost | 커뮤니케이션 |
| Notion     | 일정 및 문서 관리 |

</br>

<h4>📱 FrontEnd</h4>

| 상세            |  버전  |  비고  |
| :-------------- | :----: | :----- |
| Kotlin          |  1.8   ||
| JDK             |   17   ||
| min SDK         |  29    ||
| target SDK      |  33    ||
| MediaPipe       |  latest||
| Tensorflow Lite |  2.14.0||
| Naver CLOVA     |  1.1.6 | CLOVA Speech Recognition(CSR)  |


<h4>📱 Machine Learning</h4>

| 상세            |  버전  |  비고  |
| :-------------- | :----: | :----- |
|Python           |  3.11.6||
|opencv-contrib-python|4.8.1.78||
|Tensorflow       |  2.14.0||
|Mediapipe        | 0.10.7||
|Matplotlib       | 3.8.0||



## **4️⃣ 설치**

[SONTOK 다운로드](https://play.google.com/store/apps/details?id=com.sts.sontalksign&pcampaignid=web_share) 해당 Google Play링크에서 APK 파일을 다운받아 안드로이드 환경에서 설치할 수 있습니다.

## **5️⃣ 사용 예시**
![example](./assets/example.png)
- [Merge Request 보러 가기](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/merge_requests)
- [VoicePassing 직접 사용해 보기](https://k8a607.p.ssafy.io/)
- [Issue 바로가기](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/issues)
- [VoicePassing FAQ 확인하기](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/wikis/FAQ)
- 더 자세한 내용은 [WIKI](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/wikis/home)를 참고하세요.

## **6️⃣ 릴리즈 히스토리**

* 1.1.9
    * 최종 릴리즈 버전

## **7️⃣ 팀원 정보 및 업무 분담**
|이름|역할|담당|비고|
|:---|:---|:---|:---|
|탁성건(팀장)|ML||profornnan@gmail.com|
|강성구|ML|modeling, data 전처리, 모델 버전 관리, 파일구조 관리|sungku2757@gmail.com|
|이재홍|ML||h78749891@gmail.com|
|김용우|Android App|Figma 기획 및 제작, Front-End(media pipe, tensorflow lite), 모델 테스트|greenlife126@gmail.com|
|동화영|Android App|Figma 기획 및 제작, Front-End(MediaPipe 적용, TTS 적용, MediaPipe의 결과를 ML 입력데이터로 변환)|donghwayeong@gmail.com|
|임서희|Android App|Figma 기획 및 제작, Front-End|seooh212@gmail.com|

## **8️⃣ 회고**
|프로필|내용|비고|
|:---|:---|:---|
| <a href="https://github.com/profornnan"><img src="https://avatars.githubusercontent.com/u/59037261?v=4" width="100px;" alt="">| **탁성건**<br />   ||
| <a href="https://github.com/L1m3Kun"><img src="https://avatars.githubusercontent.com/u/113879996?v=4" width="100px;" alt="">| **강성구**<br />새로운 분야에 대한 도전이어서 어려움이 많았지만 팀원들과 함께라서 한걸음 한걸음 나아갈 수 있었습니다.   ||
| <a href="https://github.com/soybean33"><img src="https://avatars.githubusercontent.com/u/80668684?v=4?s=100" width="100px;" alt=""/> | **김용우**<br />공통, 특화를 거쳐 SSAFY에서의 마지막 프로젝트인 자율 프로젝트를 마무리 지었습니다.<br/>프로젝트를 진행하며 그 동안 쌓았던 노하우와 기술을 마음껏 쏟아 부을 수 있는 좋은 기회였습니다. 팀원들이 모두 열심히 해 주어서 프로젝트가 잘 마무리 될 수 있었습니다.<br/>다만, 프로젝트를 마무리 할 때는 언제나,, "조금만 더 시간이 있었다면..." 이라는 아쉬움이 남는 것 같습니다.<br/>온 디바이스에서 3개의 인공지능 모델(media pipe pose, media pipe hand * 2, 우리의 model)과 클로바 X의 STT, TTS 를 모두 적용해 보는 좋은 경험이였습니다.   |
| <a href="https://github.com/HwayeongD"><img src="https://avatars.githubusercontent.com/u/121176739?v=4?s=100" width="100px;" alt="">| **동화영**<br />팀원들과 자율적으로 주제를 정하며 누군가의 삶에 도움이 되는 서비스를 만들자는 하나의 목표를 정하였습니다. 지금껏 개개인이 쌓아온 기술력을 총집합하여 하나의 애플리케이션에 녹아낼 수 있는 소중한 기회였습니다.<br/>처음 적용해보는 STT, TTS, MediaPipe 기술을 안드로이드 온디바이스 환경으로 적용해보는 도전적인 과제이었으나, 실사용자들이 우리의 서비스를 사용하면서 편리하게 소통할 수 있을 모습을 상상하며 끊임없이 도전한 프로젝트였습니다.     ||
| <a href="https://github.com/h78749891"><img src="https://avatars.githubusercontent.com/u/125847525?v=4" width="100px;" alt="">| **이재홍**<br />   ||
| <a href="https://github.com/seooh99"><img src="https://avatars.githubusercontent.com/u/122509553?v=4" width="100px;" alt="">| **임서희**<br />STT와 TTS 그리고 실시간 통신을 통한 수어 번역 프로젝트를 경험할 수 있었고 즐겁게 프로젝트를 마무리 한 것 같습니다.   ||

