# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-08-http-client` |
| Lesson | 8 — Enrollment → Course HTTP client |
| Parent | `lesson-07-database-per-service` |
| What changed | `CourseClient` (`RestClient`) with connect/read timeouts and bounded retry; enroll checks published course first; unpublished/missing → `422` `COURSE_NOT_ENROLLABLE`; Course down after retries → `503` `COURSE_UNAVAILABLE`. Compose sets `COURSE_SERVICE_BASE_URL=http://course-service:8081` (DNS, not Eureka). Keeps `ddl/`. |

| Service | Port | Database |
|---------|------|----------|
| `course-service` | 8081 | `course_db` |
| `enrollment-service` | 8082 | `enrollment_db` |

## DDL

See [`ddl/README.md`](ddl/README.md). Compose mounts `ddl/` on an empty Postgres volume. Demo credentials: `.env.example`.

## Run with Compose

```bash
docker compose up --build
```

Enrollment waits until Course is healthy, then calls it over the Compose network.

## Run from the host

```bash
./mvnw -N install
./mvnw -pl common install -DskipTests
./mvnw -pl course-service spring-boot:run
# other terminal
./mvnw -pl enrollment-service spring-boot:run
```

Requires **JDK 21**. Host default: `COURSE_SERVICE_BASE_URL=http://localhost:8081`. Do not add `-am` to `spring-boot:run`.

```bash
curl -s -X POST http://localhost:8082/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"studentUsername":"an.student","courseId":1}'
# draft course id 3 → 422 COURSE_NOT_ENROLLABLE
```

## Test

```bash
./mvnw -q test
```

Tests use H2 and mock `CourseClient` / `MockRestServiceServer` — no live Course process required.

Demo users: `an.student` (student), `bao.teacher` (instructor). This lesson does not enforce roles.
