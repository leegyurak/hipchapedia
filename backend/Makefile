.PHONY: help build test run clean lint format docker-build docker-up docker-down docker-logs docker-clean

# 기본 타겟
.DEFAULT_GOAL := help

# 색상 정의
CYAN := \033[0;36m
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

help: ## 사용 가능한 명령어 목록 표시
	@echo "$(CYAN)=====================================$(NC)"
	@echo "$(CYAN)  Hipchapedia Makefile 명령어$(NC)"
	@echo "$(CYAN)=====================================$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "$(GREEN)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""

# 빌드 및 테스트
build: ## 프로젝트 빌드
	@echo "$(CYAN)Building project...$(NC)"
	./gradlew build

build-skip-test: ## 테스트 없이 빌드
	@echo "$(CYAN)Building project (skipping tests)...$(NC)"
	./gradlew build -x test

test: ## 테스트 실행
	@echo "$(CYAN)Running tests...$(NC)"
	./gradlew test

test-verbose: ## 테스트 실행 (상세 출력)
	@echo "$(CYAN)Running tests with verbose output...$(NC)"
	./gradlew test --info

coverage: ## 테스트 커버리지 리포트 생성
	@echo "$(CYAN)Generating test coverage report...$(NC)"
	./gradlew jacocoTestReport
	@echo "$(GREEN)Coverage report: build/reports/jacoco/test/html/index.html$(NC)"

clean: ## 빌드 아티팩트 정리
	@echo "$(CYAN)Cleaning build artifacts...$(NC)"
	./gradlew clean
	@echo "$(GREEN)Clean completed!$(NC)"

# 실행
run: ## 애플리케이션 실행 (로컬)
	@echo "$(CYAN)Starting application...$(NC)"
	./gradlew bootRun

dev: ## 개발 모드로 실행 (hot reload)
	@echo "$(CYAN)Starting application in dev mode...$(NC)"
	./gradlew bootRun --continuous

# 코드 품질
lint: ## ktlint 검사
	@echo "$(CYAN)Running ktlint check...$(NC)"
	./gradlew ktlintCheck

format: ## 코드 자동 포맷팅
	@echo "$(CYAN)Formatting code with ktlint...$(NC)"
	./gradlew ktlintFormat
	@echo "$(GREEN)Code formatted!$(NC)"

check: ## 전체 코드 품질 검사 (lint + test)
	@echo "$(CYAN)Running all checks...$(NC)"
	./gradlew check

# Docker 관련
docker-build: ## Docker 이미지 빌드
	@echo "$(CYAN)Building Docker image...$(NC)"
	docker build -t hipchapedia:latest .
	@echo "$(GREEN)Docker image built: hipchapedia:latest$(NC)"

docker-up: ## Docker Compose로 실행 (MySQL + Backend)
	@echo "$(CYAN)Starting services with Docker Compose...$(NC)"
	docker-compose -f docker-compose.dev.yml up -d
	@echo "$(GREEN)Services started! Check logs with: make docker-logs$(NC)"

docker-up-build: ## Docker Compose로 빌드 후 실행
	@echo "$(CYAN)Building and starting services...$(NC)"
	docker-compose -f docker-compose.dev.yml up -d --build
	@echo "$(GREEN)Services started!$(NC)"

docker-down: ## Docker Compose 중지
	@echo "$(YELLOW)Stopping services...$(NC)"
	docker-compose -f docker-compose.dev.yml down

docker-down-clean: ## Docker Compose 중지 및 볼륨 삭제
	@echo "$(RED)Stopping services and removing volumes...$(NC)"
	docker-compose -f docker-compose.dev.yml down -v
	@echo "$(GREEN)Services stopped and volumes removed!$(NC)"

docker-logs: ## Docker Compose 로그 확인
	docker-compose -f docker-compose.dev.yml logs -f

docker-logs-backend: ## Backend 로그만 확인
	docker-compose -f docker-compose.dev.yml logs -f backend

docker-logs-db: ## DB 로그만 확인
	docker-compose -f docker-compose.dev.yml logs -f db

docker-ps: ## 실행 중인 컨테이너 확인
	docker-compose -f docker-compose.dev.yml ps

docker-restart: ## 서비스 재시작
	@echo "$(CYAN)Restarting services...$(NC)"
	docker-compose -f docker-compose.dev.yml restart

docker-clean: ## Docker 이미지 및 컨테이너 정리
	@echo "$(RED)Cleaning Docker resources...$(NC)"
	docker-compose -f docker-compose.dev.yml down -v --rmi local
	@echo "$(GREEN)Docker cleanup completed!$(NC)"

# 데이터베이스
db-console: ## MySQL 콘솔 접속
	@echo "$(CYAN)Connecting to MySQL...$(NC)"
	docker-compose -f docker-compose.dev.yml exec db mysql -u hipchapedia_user -phipchapedia_password hipchapedia

db-shell: ## MySQL 컨테이너 쉘 접속
	docker-compose -f docker-compose.dev.yml exec db bash

# Flyway 마이그레이션
flyway-info: ## Flyway 마이그레이션 상태 확인
	@echo "$(CYAN)Checking Flyway migration status...$(NC)"
	./gradlew flywayInfo

flyway-migrate: ## Flyway 마이그레이션 실행
	@echo "$(CYAN)Running Flyway migration...$(NC)"
	./gradlew flywayMigrate
	@echo "$(GREEN)Migration completed!$(NC)"

flyway-validate: ## Flyway 마이그레이션 검증
	@echo "$(CYAN)Validating Flyway migrations...$(NC)"
	./gradlew flywayValidate
	@echo "$(GREEN)Validation passed!$(NC)"

flyway-clean: ## Flyway 데이터베이스 정리 (주의: 모든 데이터 삭제)
	@echo "$(RED)WARNING: This will delete all data in the database!$(NC)"
	@echo "$(YELLOW)Are you sure? [y/N]$(NC)" && read ans && [ $${ans:-N} = y ]
	./gradlew flywayClean
	@echo "$(GREEN)Database cleaned!$(NC)"

flyway-baseline: ## Flyway 베이스라인 설정
	@echo "$(CYAN)Setting Flyway baseline...$(NC)"
	./gradlew flywayBaseline
	@echo "$(GREEN)Baseline set!$(NC)"

flyway-repair: ## Flyway 메타데이터 복구
	@echo "$(CYAN)Repairing Flyway metadata...$(NC)"
	./gradlew flywayRepair
	@echo "$(GREEN)Metadata repaired!$(NC)"

db-reset: flyway-clean flyway-migrate ## 데이터베이스 리셋 (clean + migrate)
	@echo "$(GREEN)Database reset completed!$(NC)"

# 유틸리티
jar: ## JAR 파일 생성
	@echo "$(CYAN)Building JAR file...$(NC)"
	./gradlew bootJar
	@echo "$(GREEN)JAR file: build/libs/hipchapedia-0.1.0.jar$(NC)"

jar-run: jar ## JAR 파일 빌드 후 실행
	@echo "$(CYAN)Running JAR file...$(NC)"
	java -jar build/libs/hipchapedia-0.1.0.jar

deps: ## 의존성 확인
	./gradlew dependencies

deps-insight: ## 특정 의존성 분석 (example: make deps-insight DEP=org.springframework.boot:spring-boot-starter-web)
	./gradlew dependencyInsight --dependency $(DEP)

upgrade-deps: ## 의존성 업그레이드 확인
	./gradlew dependencyUpdates

# 통합 명령어
all: clean lint test build ## 전체 빌드 파이프라인 실행

install: ## 초기 설정 (Gradle Wrapper 다운로드)
	@echo "$(CYAN)Installing Gradle Wrapper...$(NC)"
	./gradlew wrapper --gradle-version 8.11
	@echo "$(GREEN)Installation completed!$(NC)"

ci: clean check build ## CI 파이프라인 (clean + check + build)
	@echo "$(GREEN)CI pipeline completed successfully!$(NC)"

# 개발 환경 설정
setup: ## 개발 환경 초기 설정
	@echo "$(CYAN)Setting up development environment...$(NC)"
	@if [ ! -f .env ]; then \
		echo "$(YELLOW)Creating .env file from .env.example...$(NC)"; \
		cp .env.example .env; \
		echo "$(YELLOW)Please edit .env file and set ANTHROPIC_API_KEY$(NC)"; \
	else \
		echo "$(GREEN).env file already exists$(NC)"; \
	fi
	@echo "$(GREEN)Setup completed!$(NC)"

info: ## 프로젝트 정보 표시
	@echo "$(CYAN)Project: Hipchapedia$(NC)"
	@echo "$(CYAN)Language: Kotlin 2.1.0$(NC)"
	@echo "$(CYAN)Framework: Spring Boot 3.4.1$(NC)"
	@echo "$(CYAN)JDK: Java 21$(NC)"
	@echo "$(CYAN)Build Tool: Gradle 8.11$(NC)"
	@echo ""
	@./gradlew --version
