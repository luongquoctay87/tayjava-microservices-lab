# File: course-service/Dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace

COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY course-service/pom.xml course-service/pom.xml
COPY enrollment-service/pom.xml enrollment-service/pom.xml
COPY bff-service/pom.xml bff-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY common/src common/src
COPY course-service/src course-service/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -pl course-service -am package -DskipTests

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache wget \
    && addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/course-service/target/course-service-*.jar app.jar
USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
