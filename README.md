# SONTOK
![header](./assets/header.png)

> 실시간 수어 번역 애플리케이션

## 주요기능

- *농인과 *청인의 실시간 대화를 도와주는 애플리케이션
- 대화의 전체 내용을 저장 및 열람하는 기능

*농인: 청각장애인 중 한국수어를 제1언어로 사용하고 한국수어에 기반한 독특한 농문화를 영위하며 사는 사람
*청인: 농인에 대비되는 개념으로 음성언어를 중심으로 의사소통을 하는 사람

## 세부기능
|구분|기능|설명|비고|
|:---|:---|:---|:---|
|1|실시간 수어 번역|농인의 수어를 텍스트로 번역, 텍스트를 음성으로 변환하여 청인에게 전달||
|2|음성을 텍스트로의 변환|청인의 음성을 텍스트로 변환하여 농인에게 전달하는 기능||
|3|대화 내용 저장|실시간 대화 내용 전체를 저장|수어, 음성 대화를 일괄 텍스트화하여 저장|
|4|대화 내용 열람|저장한 대화 내용을 불러오기||

## 아키텍처
### 시스템 다이어그램
![systemflow]()

## 설치

[SONTOK 다운로드](https://play.google.com/store/apps/details?id=com.sts.sontalksign&pcampaignid=web_share) 해당 Google Play링크에서 APK 파일을 다운받아 안드로이드 환경에서 설치할 수 있습니다.

## 사용 예시
![example](./assets/example.png)
- [Merge Request 보러 가기](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/merge_requests)
- [VoicePassing 직접 사용해 보기](https://k8a607.p.ssafy.io/)
- [Issue 바로가기](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/issues)
- [VoicePassing FAQ 확인하기](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/wikis/FAQ)
- 더 자세한 내용은 [WIKI](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/wikis/home)를 참고하세요.

## 개발 설정
자세한 내용은 [포팅 메뉴얼](https://lab.ssafy.com/ssafy_opensource/8th_voicepassing/-/blob/master/exec/%ED%8F%AC%ED%8C%85%EB%A9%94%EB%89%B4%EC%96%BC_VoicePassing.pdf)을 참고하세요.

## 릴리즈 히스토리

* 1.1.9
    * Project Transfer(프로젝트 이관)

## 기여
1. 해당 프로젝트를 Fork 하세요. ()
    * **Project URL**은 개인 Gitlab ID로 설정해주세요!
    * **Project slug**는 변경하지 않습니다.
2.  새로운 브랜치를 생성하세요.
    *   이때 브랜치는 `dev`브랜치에서 분기해주세요.
    *   브랜치 네이밍은 `기수_팀코드_학번`으로 설정합니다.    
    *   ```
        git checkout -b dev  
        git pull origin dev  
        git checkout -b 9th_A101_0911111

3.  변경사항을 commit 하세요 (`git commit -am 'Add some fooBar'`)
4.  브랜치에 Push 하세요 (`git push origin 9th_A101_0911111`)
5.  새로운 Merge Request를 요청하세요
    *   Source branch: `개인ID/프로젝트`에서 새로 생성한 브랜치(`9th_A101_0911111`)
    *   Target branch: `ssafy_opensource/프로젝트` 의 `dev` 브랜치
    *   Description에 수정된 파일의 위치와 변경 사항, 의견 등을 작성해 등록합니다.


## 소감
|이름|담당|소감|비고|
|:---|:---|:---|:---|
|탁성건||||
|강성구||||
|김용우||||
|동화영|안드로이드 애플리케이션 개발, MediaPipe 적용, TTS 적용, MediaPipe의 결과를 ML 입력데이터로 변환||donghwayeong@gmail.com|
|이재홍||||
|임서희||||

## 라이선스

Distributed under the SGPL license. See [License]() for more information.
