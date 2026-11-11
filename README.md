# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-12-idempotency` |
| Lesson | 12 — Idempotency on enroll |
| Parent | `lesson-10-api-contract` |
| What changed | `Idempotency-Key` required on `POST /api/v1/enrollments`; replay returns the same enrollment; same key + different payload → `409 IDEMPOTENCY_KEY_CONFLICT`. `PaymentStub` charges once per key. Keeps `/api/v1` contract and `ddl/`. |

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

Requires **JDK 21**. Host default: `COURSE_SERVICE_BASE_URL=http://localhost:8081`.

```bash
curl -s -X POST http://localhost:8082/api/v1/enrollments \
  -H 'Content-Type: application/json' \
  -H 'API-Version: 1' \
  -H 'Idempotency-Key: enroll-alice-course-1' \
  -d '{"studentUsername":"an.student","courseId":1}'
# repeat with the same key → same enrollment id; payment stub charges once
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
