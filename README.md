# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-13-restclient-webclient` |
| Lesson | 13 — RestClient vs WebClient |
| Parent | `lesson-12-idempotency` |
| What changed | `CourseClient` is an interface with switchable `RestCourseClient` (default) and `WebCourseClient` via `tayjava.clients.course-http-client=rest\|web` (or `COURSE_HTTP_CLIENT`). Enroll path and Idempotency-Key behavior unchanged. Keeps `ddl/`. |

| Service | Port | Database |
|---------|------|----------|
| `course-service` | 8081 | `course_db` |
| `enrollment-service` | 8082 | `enrollment_db` |

## DDL

See [`ddl/README.md`](ddl/README.md).

## Run from the host

```bash
./mvnw -N install
./mvnw -pl common install -DskipTests
./mvnw -pl course-service spring-boot:run
COURSE_HTTP_CLIENT=web ./mvnw -pl enrollment-service spring-boot:run
```

Requires **JDK 21**. Default client stack is RestClient.

```bash
curl -s -X POST http://localhost:8082/api/v1/enrollments \
  -H 'Content-Type: application/json' \
  -H 'API-Version: 1' \
  -H 'Idempotency-Key: enroll-alice-course-1' \
  -d '{"studentUsername":"an.student","courseId":1}'
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
