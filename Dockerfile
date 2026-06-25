ARG SERVICE_NAME

FROM gradle:8.14-jdk21 AS builder
WORKDIR /app

ARG SERVICE_NAME

COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY settings.gradle.kts build.gradle.kts ./

COPY common-libs/ common-libs/

COPY order-service/ order-service/
COPY payment-service/ payment-service/
COPY delivery-service/ delivery-service/

RUN chmod +x gradlew

RUN gradle :${SERVICE_NAME}:bootJar --no-daemon -x test \
    --configure-on-demand \
    --build-cache

FROM eclipse-temurin:21-jre

ARG SERVICE_NAME
WORKDIR /app

RUN echo "Copying from: /app/${SERVICE_NAME}/build/libs/*.jar"

COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]