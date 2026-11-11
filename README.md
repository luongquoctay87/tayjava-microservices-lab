# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-10-api-contract` |
| Lesson | 10 — Stable API contract / versioning |
| Parent | `lesson-08-http-client` |
| What changed | Public REST moved to `/api/v1/...`; optional `API-Version: 1` header must match when present; response echoes `API-Version`. Enrollment `CourseClient` calls `/api/v1/courses/{id}` with the header. Keeps `ddl/` and Compose DNS (no Eureka). OpenAPI left optional / not required for this lab tip. |

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
curl -s -X POST http://localhost:8082/api/v1/enrollments \
  -H 'Content-Type: application/json' \
  -H 'API-Version: 1' \
  -d '{"studentUsername":"an.student","courseId":1}'
# draft course id 3 → 422 COURSE_NOT_ENROLLABLE
# API-Version: 2 → 400 UNSUPPORTED_API_VERSION
```

## Test

```bash
./mvnw -q test
```

Tests use H2 and mock `CourseClient` / `MockRestServiceServer` — no live Course process required.

Demo users: `an.student` (student), `bao.teacher` (instructor); emails for later demos: `alice@example.com`, `bob@example.com`.
