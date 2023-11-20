# Release Note

## sl_model

- dummy data(acc -> 0)

## v4

- 2 labels
  
  > actions: ['석사', '연구']
- calculate angle as `float32`
- epoach : 200

## v5

- 15 labels
  > actions: {'가다': 0, '강아지': 1, '단추': 2, '덥다': 3, '돼지': 4, '먹다': 5, '물음표': 6, '반갑다': 7, '아침': 8, '오늘': 9,
  > '오후': 10, '저녁': 11, '졸리다': 12, '좋다': 13, '할아버지': 14}
- calculate angle as `float32`
- epoach : 200

## v6

- 15 labels
  - actions: {'가다': 0, '강아지': 1, '단추': 2, '덥다': 3, '돼지': 4, '먹다': 5, '물음표': 6, '반갑다': 7, '아침': 8, '오늘': 9, '오후': 10, '저녁': 11, '졸리다': 12, '좋다': 13, '할아버지': 14}
- calculate angle as `float16`
- epoach : 100

## v7

- 15 labels
  - actions: {'가다': 0, '강아지': 1, '단추': 2, '덥다': 3, '돼지': 4, '먹다': 5, '물음표': 6, '반갑다': 7, '아침': 8, '오늘': 9, '오후': 10, '저녁': 11, '졸리다': 12, '좋다': 13, '할아버지': 14}
- calculate angle as `float16`
- epoach : 200

## v8

- 15 labels
  - actions: {'가다': 0, '강아지': 1, '단추': 2, '덥다': 3, '돼지': 4, '먹다': 5, '물음표': 6, '반갑다': 7, '아침': 8, '오늘': 9, '오후': 10, '저녁': 11, '졸리다': 12, '좋다': 13, '할아버지': 14}
- use mediapipe pose heavy model

### v8_1

- calculate angle as `float16`
- epoach : 100

### v8_2

- calculate angle as `float16`
- epoach : 200

### v8_3

- calculate angle as `float32`
- epoach : 100

### v8_4

- calculate angle as `float32`
- epoach : 200

## v9

- 30s dataset(repeated)
- 20 labels(index 7-'무엇' 이슈)
  - ['', '가다', '감사합니다', '강아지', '덥다', '돼지', '먹다', '무엇', '반갑다', '석사', '아침', '안녕하세요', '연구','오늘', '오후', '저녁', '졸리다', '좋다', '질문', '할아버지']
- use mediapipe pose heavy model

### v9_1

- calculate angle as `float16`
- epoach : 100

### v9_2

- calculate angle as `float16`
- epoach : 200

### v9_3

- calculate angle as `float32`
- epoach : 100

### v9_4

- calculate angle as `float32`
- epoach : 200

## v10

- 30s dataset(repeated)
- 20 labels
  - ['', '가다', '감사합니다', '강아지', '덥다', '돼지', '먹다', '무엇', '반갑다', '석사', '아침', '안녕하세요', '연구','오늘', '오후', '저녁', '졸리다', '좋다', '질문', '할아버지']
- use mediapipe pose heavy model
- hand & pose 결과가 없어도 프레임에 포함

### v10_1

- calculate angle as `float16`
- epoach : 100

### v10_2

- calculate angle as `float16`
- epoach : 200

### v10_3

- calculate angle as `float32`
- epoach : 100

### v10_4

- calculate angle as `float32`
- epoach : 200

## v11

- 30s dataset(repeated)
- 5 labels

  - ['', '강아지', '석사', '연구', '오늘']

- use mediapipe pose heavy model
- hand & pose 결과가 없어도 프레임에 포함
-

### v11_1

- calculate angle as `float16`
- epoach : 100

### v11_2

- calculate angle as `float16`
- epoach : 150

### v11_3

- calculate angle as `float16`
- epoach : 200

### v11_4

- calculate angle as `float32`
- epoach : 100

### v11_5

- calculate angle as `float32`
- epoach : 150

### v11_6

- calculate angle as `float32`
- epoach : 200

## v12

- 30s dataset(repeated)
- 4 labels

  - ['', '석사', '연구', '오늘']

- use mediapipe pose heavy model
- `z` 축 값 무시
-

### v12_1

- calculate angle as `float16`
- epoach : 100

### v12_2

- calculate angle as `float16`
- epoach : 150

### v12_3

- calculate angle as `float16`
- epoach : 200

### v12_4

- calculate angle as `float32`
- epoach : 100

### v12_5

- calculate angle as `float32`
- epoach : 150

### v12_6

- calculate angle as `float32`
- epoach : 200

## v13

- 60s dataset(repeated)
- 17 labels

  - ["", "감사합니다", "경험", "끝내다", "담그다", "담당", "먹다", "무엇", "발표", "보다", "수제비", "에", "오늘", "정말", "제", "질문", "집", "책"]

- use mediapipe pose heavy model
- `z` 축 값 무시
- `"책"` 데이터 셋 무시당함

### v13_1

- calculate angle as `float16`
- epoach : 100

### v13_2

- calculate angle as `float16`
- epoach : 150

### v13_3

- calculate angle as `float16`
- epoach : 200

### v13_4

- calculate angle as `float32`
- epoach : 100

### v13_5

- calculate angle as `float32`
- epoach : 150

### v13_6

- calculate angle as `float32`
- epoach : 200

## v14

- 60s dataset(repeated) with z flip
- 17 labels

  - ["", "감사합니다", "경험", "끝내다", "담그다", "담당", "먹다", "무엇", "발표", "보다", "수제비", "에", "오늘", "정말", "제", "질문", "집", "책"]

- use mediapipe pose heavy model
- `z` 축 값 무시

### v14_1

- calculate angle as `float16`
- epoach : 100

### v14_2

- calculate angle as `float16`
- epoach : 150

### v14_3

- calculate angle as `float16`
- epoach : 200

### v14_4

- calculate angle as `float32`
- epoach : 100

### v14_5

- calculate angle as `float32`
- epoach : 150

### v14_6

- calculate angle as `float32`
- epoach : 200

## v14

- 60s dataset(repeated) with z flip
- 17 labels

  - ["", "감사합니다", "경험", "끝내다", "담그다", "담당", "먹다", "무엇", "발표", "보다", "수제비", "에", "오늘", "정말", "제", "질문", "집", "책"]

- use mediapipe pose heavy model
- `z` 축 값 무시
- lag 분할 테스트

### 1 lag

#### v14_1

- calculate angle as `float16`
- epoach : 100

#### v14_2

- calculate angle as `float16`
- epoach : 150

#### v14_3

- calculate angle as `float16`
- epoach : 200

#### v14_4

- calculate angle as `float32`
- epoach : 100

#### v14_5

- calculate angle as `float32`
- epoach : 150

#### v14_6

- calculate angle as `float32`
- epoach : 200

### 3 lag

#### v14_1

- calculate angle as `float16`
- epoach : 100

#### v14_2

- calculate angle as `float16`
- epoach : 150

#### v14_3

- calculate angle as `float16`
- epoach : 200

#### v14_4

- calculate angle as `float32`
- epoach : 100

#### v14_5

- calculate angle as `float32`
- epoach : 150

#### v14_6

- calculate angle as `float32`
- epoach : 200

### 5 lag

#### v14_1

- calculate angle as `float16`
- epoach : 100

#### v14_2

- calculate angle as `float16`
- epoach : 150

#### v14_3

- calculate angle as `float16`
- epoach : 200

#### v14_4

- calculate angle as `float32`
- epoach : 100

#### v14_5

- calculate angle as `float32`
- epoach : 150

#### v14_6

- calculate angle as `float32`
- epoach : 200

### 10 lag

#### v14_1

- calculate angle as `float16`
- epoach : 100

#### v14_2

- calculate angle as `float16`
- epoach : 150

#### v14_3

- calculate angle as `float16`
- epoach : 200

#### v14_4

- calculate angle as `float32`
- epoach : 100

#### v14_5

- calculate angle as `float32`
- epoach : 150

#### v14_6

- calculate angle as `float32`
- epoach : 200



## v15

- 여러개의 파일로 구성
- '' 값과 '.' 포함
- 8 labels

  - ["", ".", "끝내다", "담당", "발표", "제", "준비", "?"]

- use mediapipe pose heavy model
- `z` 축 값 무시
- lag 분할

### 1 lag
#### v15_1

- calculate angle as `float16`
- epoach : 100

#### v15_2

- calculate angle as `float16`
- epoach : 150

#### v15_3

- calculate angle as `float16`
- epoach : 200

#### v15_4

- calculate angle as `float32`
- epoach : 100

#### v15_5

- calculate angle as `float32`
- epoach : 150

#### v15_6

- calculate angle as `float32`
- epoach : 200
### 3 lags
#### v15_1

- calculate angle as `float16`
- epoach : 100

#### v15_2

- calculate angle as `float16`
- epoach : 150

#### v15_3

- calculate angle as `float16`
- epoach : 200

#### v15_4

- calculate angle as `float32`
- epoach : 100

#### v15_5

- calculate angle as `float32`
- epoach : 150

#### v15_6

- calculate angle as `float32`
- epoach : 200
### 5 lags
#### v15_1

- calculate angle as `float16`
- epoach : 100

#### v15_2

- calculate angle as `float16`
- epoach : 150

#### v15_3

- calculate angle as `float16`
- epoach : 200

#### v15_4

- calculate angle as `float32`
- epoach : 100

#### v15_5

- calculate angle as `float32`
- epoach : 150

#### v15_6

- calculate angle as `float32`
- epoach : 200
### 10 lags
#### v15_1

- calculate angle as `float16`
- epoach : 100

#### v15_2

- calculate angle as `float16`
- epoach : 150

#### v15_3

- calculate angle as `float16`
- epoach : 200

#### v15_4

- calculate angle as `float32`
- epoach : 100

#### v15_5

- calculate angle as `float32`
- epoach : 150

#### v15_6

- calculate angle as `float32`
- epoach : 200

## v16
- change model & input shape
### lag 1
#### v16_2
- calculate angle as `float16`
- epoach : 150
