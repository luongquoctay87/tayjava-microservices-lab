# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-06-compose` |
| Lesson | 6 — Docker Compose + multi-stage images |
| Parent | `master` |
| What changed | Root `compose.yaml` (project name `tayjava-microservices-lab`), Dockerfile per service with build context that copies `common`, Actuator healthchecks on `/actuator/health`. Data is still in memory; services do not call each other yet. |

| Service | Port | Owns |
|---------|------|------|
| `course-service` | 8081 | courses |
| `enrollment-service` | 8082 | enrollments (`courseId` only) |

## Run with Compose

Requires **JDK 21** for host builds; Compose builds images with Maven Temurin 21.

```bash
docker compose up --build
```

The image build copies the `common` module because both services depend on it. Healthchecks call `/actuator/health` with `wget` inside the JRE Alpine image. Discovery between containers later uses Compose DNS service names — not Eureka.

Stop the stack with `docker compose down`.

## Run from the host

```bash
./mvnw -N install
./mvnw -pl common install -DskipTests
./mvnw -pl course-service spring-boot:run
# other terminal
./mvnw -pl enrollment-service spring-boot:run
```

Do not add `-am` to `spring-boot:run` on a service module.

```bash
curl -s http://localhost:8081/api/courses
curl -s http://localhost:8081/actuator/health
curl -s -X POST http://localhost:8082/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"studentUsername":"an.student","courseId":1}'
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` (student), `bao.teacher` (instructor). This lesson does not enforce roles.
