import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
    id("org.flywaydb.flyway") version "10.21.0"
}

group = "com.hipchapedia"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")

    // Jackson for Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // MySQL
    runtimeOnly("com.mysql:mysql-connector-j")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // HTTP Client for Claude API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.lettuce:lettuce-core")

    // Configuration
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // H2 for testing
    testImplementation("com.h2database:h2")
}

ktlint {
    version.set("1.5.0")
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(21))
        },
    )
}

// ktlint 체크를 빌드 프로세스에 통합
tasks.named("check") {
    dependsOn("ktlintCheck")
}

// Flyway 설정
flyway {
    url =
        System.getenv("DB_URL")
            ?: "jdbc:mysql://localhost:3306/hipchapedia?" +
            "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    user = System.getenv("DB_USER") ?: "root"
    password = System.getenv("DB_PASSWORD") ?: "password"
    locations = arrayOf("classpath:db/migration")
    cleanDisabled = false
}
