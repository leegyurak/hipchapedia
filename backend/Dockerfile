# Build stage
FROM gradle:8.11-jdk21 AS build

WORKDIR /app

# Gradle 의존성 캐싱을 위해 먼저 복사
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY src ./src

# 빌드 실행
RUN gradle build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8000

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
