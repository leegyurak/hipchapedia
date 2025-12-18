# Hipchapedia (힙차피디아)

Kotlin + Spring Boot 기반 Clean Architecture로 구현된 힙합 가사 분석 백엔드 API 서버입니다.

## 기능

- 힙합 곡 제목과 가사를 입력받아 Claude 4.5 Haiku API로 분석
- 가사 철학, 라임, 펀치라인, 장점, 단점, **별점**, **이동진 스타일 한줄평** 등을 Markdown 형식으로 반환
- MySQL 데이터베이스에 가사 정보 저장 (SHA-256 해시 기반 중복 방지)
- **분석 결과 캐싱**: 동일한 가사에 대해 이미 분석된 경우 저장된 결과 반환
- Entity-DTO 변환을 위한 Mapper 패턴 적용
- Kotlin Coroutines 기반 비동기 처리

## 아키텍처

Clean Architecture를 따르는 계층 구조:

```
src/main/kotlin/com/hipchapedia/
├── domain/                 # 도메인 레이어
│   ├── entities/           # 엔티티
│   ├── services/           # 비즈니스 로직
│   ├── interfaces/         # 인터페이스 정의
│   └── utils/              # 도메인 유틸리티
├── infrastructure/         # 인프라 레이어
│   ├── ai/                 # Claude API 클라이언트
│   ├── db/                 # 데이터베이스
│   │   ├── models/         # JPA Entity
│   │   └── repositories/   # Spring Data JPA Repository
│   └── config/             # 설정 클래스
├── application/            # 애플리케이션 레이어
│   ├── dtos/               # Data Transfer Objects
│   └── mappers/            # Entity-DTO 변환 Mapper
└── presentation/           # 프레젠테이션 레이어
    └── api/                # REST API
        ├── controllers/    # Controllers
        ├── advice/         # Exception Handlers
        └── dto/            # API DTOs
```

## 기술 스택

- **Language**: Kotlin 2.1.0
- **Framework**: Spring Boot 3.4.1
- **JDK**: Java 21
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA (Hibernate)
- **Migration**: Flyway 10.21.0
- **AI**: Anthropic Claude 4.5 Haiku
- **Build Tool**: Gradle 8.11
- **Code Quality**: ktlint 1.5.0
- **Testing**: JUnit 5, MockK, Spring MockK

## 빠른 시작 (Makefile 사용)

프로젝트에 Makefile이 포함되어 있어 간편하게 명령어를 실행할 수 있습니다.

```bash
# 사용 가능한 모든 명령어 확인
make help

# 개발 환경 초기 설정 (.env 파일 생성)
make setup

# Docker Compose로 실행
make docker-up-build

# 로그 확인
make docker-logs

# 종료
make docker-down
```

### 주요 Makefile 명령어

| 명령어 | 설명 |
|--------|------|
| `make help` | 사용 가능한 모든 명령어 표시 |
| `make setup` | 초기 개발 환경 설정 (.env 생성) |
| `make build` | 프로젝트 빌드 |
| `make test` | 테스트 실행 |
| `make run` | 로컬에서 애플리케이션 실행 |
| `make lint` | ktlint 코드 검사 |
| `make format` | 코드 자동 포맷팅 |
| `make docker-up-build` | Docker Compose 빌드 후 실행 |
| `make docker-logs` | Docker 로그 확인 |
| `make docker-down` | Docker Compose 중지 |
| `make clean` | 빌드 아티팩트 정리 |
| `make all` | 전체 빌드 파이프라인 실행 |
| `make flyway-migrate` | Flyway 마이그레이션 실행 |
| `make flyway-info` | Flyway 마이그레이션 상태 확인 |
| `make db-reset` | 데이터베이스 리셋 (clean + migrate) |

## 설치 및 실행

### 방법 1: Docker Compose (권장)

가장 빠르고 간편한 방법입니다. MySQL 설치 없이 바로 실행 가능합니다.

```bash
# Makefile 사용
make setup              # .env 파일 생성
make docker-up-build    # 빌드 후 실행

# 또는 직접 명령어 실행
export ANTHROPIC_API_KEY=your-api-key-here
docker-compose -f docker-compose.dev.yml up -d --build

# 로그 확인
make docker-logs
# 또는
docker-compose -f docker-compose.dev.yml logs -f

# 종료
make docker-down
# 또는
docker-compose -f docker-compose.dev.yml down
```

서버는 http://localhost:8000 에서 접근 가능합니다.

### 방법 2: 로컬 개발 환경

로컬에서 직접 실행하려면 MySQL을 먼저 설치해야 합니다.

#### 1. 환경 변수 설정

`.env.example` 파일을 참고하여 `.env` 파일을 생성하세요:

```bash
# .env.example을 복사
cp .env.example .env

# .env 파일을 열어 필요한 값을 설정
# 특히 ANTHROPIC_API_KEY는 필수입니다
```

#### 2. MySQL 데이터베이스 생성

```bash
# MySQL에 접속하여 데이터베이스 생성
mysql -u root -p
CREATE DATABASE hipchapedia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 3. 애플리케이션 실행

```bash
# Makefile 사용
make run

# 또는 Gradle을 직접 사용
./gradlew bootRun

# JAR 파일 빌드 후 실행
make jar-run
# 또는
./gradlew build
java -jar build/libs/hipchapedia-0.1.0.jar
```

## API 사용법

### 가사 분석 API

**Endpoint**: `POST /api/lyrics/analyze`

**Request Body**:
```json
{
  "title": "곡 제목",
  "lyrics": "가사 내용\n여러 줄로\n작성 가능"
}
```

**Response**:
```json
{
  "title": "곡 제목",
  "lyrics": "가사 내용\n여러 줄로\n작성 가능",
  "analysisResult": "# 곡 제목 분석\n\n## 가사 철학\n...\n\n## 별점\n★★★★☆ (4/5)\n\n## 한줄평\n타고난 플로우 감각이 거친 현실을 부드럽게 감싸 안는다."
}
```

### cURL 예제

```bash
curl -X POST http://localhost:8000/api/lyrics/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "title": "테스트 곡",
    "lyrics": "이것은 테스트 가사입니다"
  }'
```

## 데이터베이스 스키마

### lyrics 테이블

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | BIGINT | Primary Key (Auto Increment) |
| title | VARCHAR(200) | 곡 제목 |
| lyrics_hash | VARCHAR(64) | 가사 SHA-256 해시 (UNIQUE) |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

- `lyrics_hash`는 가사의 SHA-256 해시 값입니다.
- 같은 가사의 중복 저장을 방지합니다.

### lyrics_analysis_result 테이블

| 컬럼 | 타입 | 설명 |
|------|------|------|
| lyrics_id | BIGINT | Primary Key & Foreign Key (lyrics.id, CASCADE) |
| analysis_result | TEXT | 분석 결과 (Markdown) |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

- `lyrics`와 1:1 관계 (OneToOne, `@MapsId`)
- 분석 결과를 별도 테이블로 분리하여 정규화 및 성능 최적화
- 동일한 가사에 대해 이미 분석 결과가 있으면 캐시된 결과를 반환합니다.

## 데이터베이스 마이그레이션 (Flyway)

프로젝트는 **Flyway**를 사용하여 데이터베이스 스키마를 버전 관리합니다.

### Flyway 주요 명령어

```bash
# 마이그레이션 상태 확인
make flyway-info

# 마이그레이션 실행
make flyway-migrate

# 마이그레이션 검증
make flyway-validate

# 데이터베이스 리셋 (clean + migrate)
make db-reset

# Flyway 메타데이터 복구
make flyway-repair

# 베이스라인 설정
make flyway-baseline
```

### 마이그레이션 스크립트 작성

새로운 마이그레이션 스크립트는 `src/main/resources/db/migration/` 디렉토리에 작성합니다.

**네이밍 규칙**: `V{버전}__{설명}.sql`

예시:
```
V1__create_initial_schema.sql
V2__add_user_table.sql
V3__add_rating_column.sql
```

**예제 마이그레이션 스크립트**:
```sql
-- V2__add_rating_column.sql
ALTER TABLE lyrics_analysis_result
ADD COLUMN rating DECIMAL(2,1) COMMENT '별점 (0.0-5.0)';
```

### Flyway 설정

- **JPA DDL Auto**: `validate` - Flyway가 스키마를 관리하므로 JPA는 검증만 수행
- **Baseline On Migrate**: `true` - 기존 데이터베이스에 Flyway를 적용할 때 사용
- **Validate On Migrate**: `true` - 마이그레이션 전 스크립트 검증
- **Clean Disabled**: `false` - 개발 환경에서 `flyway-clean` 허용

### 초기 마이그레이션

프로젝트를 처음 실행하면 Flyway가 자동으로 마이그레이션을 실행합니다:

```bash
# Docker Compose로 실행 시 자동 마이그레이션
make docker-up-build

# 로컬 실행 시
make flyway-migrate
make run
```

## 테스트

모든 테스트는 GWT (Given-When-Then) 패턴을 따릅니다.

```bash
# Makefile 사용
make test              # 테스트 실행
make test-verbose      # 상세 출력과 함께 테스트 실행
make coverage          # 테스트 커버리지 리포트 생성

# 또는 Gradle을 직접 사용
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests "com.hipchapedia.domain.utils.HashUtilsTest"

# 테스트 결과 확인
./gradlew test --info
```

### 테스트 구조

- **Unit Tests**:
  - `HashUtilsTest`: 가사 해시 생성 테스트
  - `LyricsAnalysisServiceTest`: 도메인 서비스 로직 테스트 (MockK 사용)
  - `LyricsAnalysisControllerTest`: REST API 컨트롤러 테스트 (Spring MockMvc 사용)

## 개발 도구

### 코드 포맷팅 및 린팅

```bash
# Makefile 사용
make lint              # ktlint 검사
make format            # 코드 자동 포맷팅
make check             # 전체 코드 품질 검사 (lint + test)

# 또는 Gradle을 직접 사용
./gradlew ktlintCheck   # ktlint 검사
./gradlew ktlintFormat  # 자동 포맷팅
./gradlew build         # 빌드 시 자동으로 ktlint 검사 실행
```

### 빌드 및 정리

```bash
make build             # 프로젝트 빌드
make build-skip-test   # 테스트 없이 빌드
make clean             # 빌드 아티팩트 정리
make all               # 전체 빌드 파이프라인 (clean + lint + test + build)
```

### 프로젝트 구조 상세

```
backend/
├── src/
│   ├── main/
│   │   ├── kotlin/com/hipchapedia/
│   │   │   ├── HipchapediaApplication.kt
│   │   │   ├── domain/
│   │   │   │   ├── entities/
│   │   │   │   │   └── LyricsAnalysis.kt
│   │   │   │   ├── services/
│   │   │   │   │   └── LyricsAnalysisService.kt
│   │   │   │   ├── interfaces/
│   │   │   │   │   ├── AIServiceInterface.kt
│   │   │   │   │   └── LyricsRepositoryInterface.kt
│   │   │   │   └── utils/
│   │   │   │       └── HashUtils.kt
│   │   │   ├── infrastructure/
│   │   │   │   ├── ai/
│   │   │   │   │   └── ClaudeService.kt
│   │   │   │   ├── db/
│   │   │   │   │   ├── models/
│   │   │   │   │   │   ├── LyricsEntity.kt
│   │   │   │   │   │   └── LyricsAnalysisResultEntity.kt
│   │   │   │   │   └── repositories/
│   │   │   │   │       ├── LyricsJpaRepository.kt
│   │   │   │   │       ├── LyricsAnalysisResultJpaRepository.kt
│   │   │   │   │       └── LyricsRepositoryImpl.kt
│   │   │   │   └── config/
│   │   │   │       └── AnthropicConfig.kt
│   │   │   ├── application/
│   │   │   │   ├── dtos/
│   │   │   │   │   └── LyricsAnalysisDTOs.kt
│   │   │   │   └── mappers/
│   │   │   │       └── LyricsAnalysisMapper.kt
│   │   │   └── presentation/
│   │   │       └── api/
│   │   │           ├── controllers/
│   │   │           │   └── LyricsAnalysisController.kt
│   │   │           ├── advice/
│   │   │           │   └── GlobalExceptionHandler.kt
│   │   │           └── dto/
│   │   │               └── ErrorResponse.kt
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── db/
│   │       │   └── migration/
│   │       │       └── V1__create_initial_schema.sql
│   │       └── prompts/
│   │           └── lyrics_analysis_prompt.md
│   └── test/
│       ├── kotlin/com/hipchapedia/
│       │   ├── domain/
│       │   │   ├── utils/
│       │   │   │   └── HashUtilsTest.kt
│       │   │   └── services/
│       │   │       └── LyricsAnalysisServiceTest.kt
│       │   └── presentation/
│       │       └── api/
│       │           └── controllers/
│       │               └── LyricsAnalysisControllerTest.kt
│       └── resources/
│           └── application-test.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .editorconfig
├── .gitignore
├── .dockerignore
├── Dockerfile
├── docker-compose.dev.yml
├── .env.example
└── README.md
```

## 주요 특징

### Clean Architecture
- 도메인 로직이 프레임워크와 독립적
- 의존성 역전 원칙(DIP)을 통한 테스트 용이성
- 각 레이어의 명확한 책임 분리

### Kotlin Coroutines
- `suspend` 함수를 사용한 비동기 처리
- `withContext(Dispatchers.IO)`로 블로킹 작업 처리
- Spring과 통합된 코루틴 지원

### Spring Boot 3.x
- 최신 Spring Boot 기능 활용
- Spring Data JPA를 통한 데이터 접근
- Bean Validation (Jakarta Validation)

### 코드 품질
- ktlint를 통한 Kotlin 코드 스타일 검사
- MockK를 사용한 단위 테스트
- Spring MockMvc를 사용한 통합 테스트

## 라이센스

MIT
