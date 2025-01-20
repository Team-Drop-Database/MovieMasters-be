plugins {
    id("java")
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.sonarqube") version "6.0.1.5171"
}

group = "com.drop_database"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

sonar {
    properties {
        property("sonar.projectKey", "se-specialization-2024-1_projects-tse3_drop-database_backend_ae7bcf3a-4eb9-416f-908b-3242b826462b")
        property("sonar.projectName", "Backend")
        property("sonar.qualitygate.wait", true)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Jakarta
    implementation("jakarta.persistence:jakarta.persistence-api")

    // okhttp
    implementation("com.squareup.okhttp3:okhttp")

    // Dotenv
    implementation("me.paulschwarz:spring-dotenv:3.0.0")

    // JWT dependencies
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5") // For Jackson JSON serialization

    // Runtime dependencies
    runtimeOnly("com.mysql:mysql-connector-j")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("org.jeasy:easy-random-core:4.0.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
