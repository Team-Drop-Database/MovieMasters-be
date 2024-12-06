FROM gradle:jdk21 AS build

WORKDIR /app

COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY settings.gradle.kts /app/settings.gradle.kts
COPY build.gradle.kts /app/build.gradle.kts

RUN ["./gradlew", "dependencies", "--no-daemon"]
COPY . /app
RUN ["./gradlew", "bootJar", "--no-daemon"]

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
