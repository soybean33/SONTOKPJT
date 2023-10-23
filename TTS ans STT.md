## TTS  

[CLOVA Voice](https://www.ncloud.com/product/aiService/clovaVoice)  

### 요금
|서비스 플랜|구분|과금 구간|과금 단위|과금 기준|요금|
|--|--|--|--|--|--|
|Premium|기본 요금|전 구간 동일|-|월|90,000 원|
|Premium|추가 요금|1,000,000개 이하|1,000|글자 당|무료|
|Premium|추가 요금|1,000,000개 초과|1,000|글자 당|100 원|

* 이용 글자는 1,000 글자 단위로 올림됩니다.  
* 이용 글자는 월 단위로 누적되어 계산됩니다.  

### API 이용
입력된 텍스트를 RESTful API 방식으로 서버에 전달하면 서버에서 인식한 텍스트를 *.mp3, *.wav 파일 확장자로 리턴해주는 API입니다. 1회 호출 시 요청할 수 있는 최대 글자는 2,000글자입니다. Volume, Speed, Pitch, Emotion 등의 감정 파라미터를 제공합니다.  

|항목|내용|
|--|--|
|리턴 형태|파일 확장자: *.mp3(기본값), *.wav|
|지원 언어|한국어, 영어, 일본어, 중국어, 스페인어, 대만어|

[개발 가이드](https://api.ncloud-docs.com/docs/ai-naver-clovavoice)


## STT(Speech-to-Text)  

[CLOVA Speech Recognition(CSR)](https://www.ncloud.com/product/aiService/csr)  

### 요금  
|구분|과금 단위|과금 기준|요금|
|--|--|--|--|
|음성 인식 이용시간 (음성 인식 요청~종료)|15|초당|4원|

* 이용 시간은 15초 단위로 올림됩니다.  
* API 호출 1회당 인식할 수 있는 음성은 60초까지 가능합니다.  
* 음성 인식 요청 후 종료까지의 시간을 측정하여 이용 시간으로 계산합니다. 즉 인식 요청하는 스트림 또는 파일의 중간에 묵음 구간 등의 공백이 있어도 이용 시간에 포함됩니다.  
* 모바일 SDK와 REST API에는 동일한 요금제가 적용됩니다.  

### API 이용 및 제공 기능  
CLOVA Speech Recognition(CSR)는 자체 개발한 스트리밍 프로토콜을 구현한 모바일 SDK와 HTTP 기반 REST API를 제공합니다. 모바일 SDK를 이용한 애플리케이션은 Client ID와 Android 애플리케이션 개발 패키지 이름을 사용하여 API 인증을 진행하며, REST API는 Client ID와 Client Secret을 이용하여 인증합니다. 암호화는 인증부터 API 이용까지 데이터 전송 구간 전체에 적용할 수 있습니다. 제공 기능은 아래와 같습니다.  

|이용 방식|지원 플랫폼|인식 가능 언어|인식 가능 시간|인식 가능 음성파일 포맷|CSR 엔진 전달 데이터|
|--|--|--|--|--|--|
|모바일 SDK|Android 2.3.3 (API 레벨 10)|한국어, 영어, 일본어, 중국어|60초|-|마이크로 입력된 음성|

[개발 가이드](https://guide.ncloud-docs.com/docs/ko/csr-overview)  