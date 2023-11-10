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
