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
