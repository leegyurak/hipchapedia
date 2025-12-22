# Lyrics Fetcher Service

Redis pub/sub 기반의 가사 검색 서비스입니다. Clean Architecture를 적용하여 구현되었습니다.

## 프로젝트 구조

```
fetcher/
├── src/
│   ├── domain/              # 도메인 레이어
│   │   ├── entities/        # 엔티티 (Song, SearchRequest)
│   │   └── repositories/    # 리포지토리 인터페이스
│   ├── use_cases/           # 유즈케이스 레이어
│   ├── infrastructure/      # 인프라 레이어
│   │   ├── external/        # 외부 API (Genius)
│   │   └── messaging/       # 메시징 (Redis)
│   ├── presentation/        # 프레젠테이션 레이어
│   ├── config.py            # 설정
│   └── main.py             # 애플리케이션 진입점
├── tests/
│   ├── unit/               # 단위 테스트
│   └── integration/        # 통합 테스트
└── pyproject.toml          # 프로젝트 설정 및 의존성
```

## Clean Architecture 레이어

### Domain Layer
- **Entities**: 비즈니스 로직의 핵심 객체 (Song, SearchRequest)
- **Repository Interfaces**: 데이터 접근을 위한 추상 인터페이스

### Use Cases Layer
- 애플리케이션의 비즈니스 로직을 담당
- SearchLyricsUseCase: 가사 검색 유즈케이스

### Infrastructure Layer
- **External**: Genius API 연동
- **Messaging**: Redis pub/sub 구현

### Presentation Layer
- LyricsFetcherService: Redis 메시지를 처리하는 서비스

## 설치

### 필수 요구사항
- Python 3.11 이상
- Redis Server

### 패키지 설치

```bash
# uv를 사용하는 경우
uv sync --all-groups

# 또는 pip를 사용하는 경우
pip install -e ".[dev]"
```

## 설정

`.env.example` 파일을 `.env`로 복사하고 필요한 값을 설정합니다:

```bash
cp .env.example .env
```

`.env` 파일 수정:
```
GENIUS_API_TOKEN=your_actual_genius_api_token
REDIS_HOST=localhost
REDIS_PORT=6379
```

## 실행

```bash
python -m src.main
```

## 사용법

### 1. 서비스 실행
```bash
python -m src.main
```

### 2. 검색 요청 보내기

Python 클라이언트 예제:
```python
import redis
import json

# Redis 클라이언트 생성
r = redis.Redis(host='localhost', port=6379, decode_responses=True)

# 검색 요청 발행
request = {
    "title": "0",
    "artist": "블랙넛"
}
r.publish('lyrics:requests', json.dumps(request, ensure_ascii=False))
```

Redis CLI 예제:
```bash
redis-cli PUBLISH lyrics:requests '{"title": "0", "artist": "블랙넛"}'
```

### 3. 결과 구독하기

Python 클라이언트 예제:
```python
import redis
import json

r = redis.Redis(host='localhost', port=6379, decode_responses=True)
pubsub = r.pubsub()
pubsub.subscribe('lyrics:results')

for message in pubsub.listen():
    if message['type'] == 'message':
        result = json.loads(message['data'])
        print(f"Title: {result['title']}")
        print(f"Artist: {result['artist']}")
        print(f"URL: {result['url']}")
```

Redis CLI 예제:
```bash
redis-cli SUBSCRIBE lyrics:results
```

## 테스트

### 모든 테스트 실행
```bash
pytest
```

### 단위 테스트만 실행
```bash
pytest tests/unit/ -v
```

### 통합 테스트만 실행 (Redis 서버 필요)
```bash
pytest tests/integration/ -v -m integration
```

### 커버리지 없이 실행
```bash
pytest --no-cov
```

## 환경 변수

| 변수 | 설명 | 기본값 |
|------|------|--------|
| GENIUS_API_TOKEN | Genius API 토큰 | - (필수) |
| REDIS_HOST | Redis 호스트 | localhost |
| REDIS_PORT | Redis 포트 | 6379 |
| REDIS_DB | Redis 데이터베이스 번호 | 0 |
| REDIS_REQUEST_CHANNEL | 요청 채널명 | lyrics:requests |
| REDIS_RESULT_CHANNEL | 결과 채널명 | lyrics:results |
| LOG_LEVEL | 로그 레벨 | INFO |

## 메시지 형식

### 요청 (lyrics:requests)
```json
{
  "title": "곡 제목",
  "artist": "아티스트명"
}
```

### 응답 (lyrics:results)
```json
{
  "title": "곡 제목",
  "artist": "아티스트명",
  "lyrics": "가사 내용",
  "url": "https://genius.com/...",
  "album": null,
  "release_date": "발매일"
}
```

## 개발

### 코드 포맷팅
```bash
ruff format .
```

### 린팅
```bash
ruff check .
```

### 타입 체킹
```bash
mypy src
```

## Docker로 실행

### Docker Compose 사용
```bash
# 전체 스택 실행 (Redis + Fetcher)
docker-compose up -d

# 로그 확인
docker-compose logs -f fetcher

# 종료
docker-compose down
```

### Docker만 사용
```bash
# Redis 시작
docker run -d --name redis -p 6379:6379 redis:7-alpine

# Fetcher 빌드 및 실행
docker build -t lyrics-fetcher .
docker run --name fetcher --link redis:redis -e REDIS_HOST=redis -e GENIUS_API_TOKEN=your_token lyrics-fetcher
```

## 라이선스

MIT
