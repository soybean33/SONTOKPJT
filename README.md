# Django 프로젝트 실행

> !주의!
> 폴더 위치가 project(`/backend`) 안에 있어야 함

1. Migration 셋팅

```bash
python manage.py makemigraions
```

2. migrate 실행

```bash
python manage.py migrate
```

3. run server

```bash
python manage.py runserver
```

# 가상환경 세팅

1. venv 설치

```bash
python -m venv venv
```

2. 가상환경 실행

```bash
source venv/Scripts/activate
```

3. 가상환경 종료

```bash
deactivate
```

# 환경 세팅

1. 다운 받는 경우(패키지 목록 다운)

```bash
pip install -r ['파일 이름'].txt
```

2. 새로운 패키지(라이브러리)를 받은 경우(패키지 목록 저장)

```bash
pip freeze > ['파일 이름'].txt
```
