# TayJava Microservices Lab — Strangler Fig demo

**Kind:** demo (side branch). **Parent:** `master`. Do **not** merge into the continuous project line (`lesson-37-compose-baseline`).

## This branch

| | |
|---|---|
| Branch | `demo/lesson-38-strangler` |
| Lesson | 38 — Strangler Fig illustration |
| Parent | `master` |
| What changed | `monolith-stub` (old writer/reader) + `strangler-gateway` with weighted GET `/api/courses` (10% extracted course-service / 90% monolith) and catch-all to monolith for non-GET writes. |

## Idea

1. Put an interception point (Gateway) in front of the modular monolith.
2. Extract the course **read** slice first; keep writes on the monolith until ownership moves.
3. Ramp weight `courses-read` toward the new service; one writer per entity.

## Run

```bash
# terminals: course-service :8081, monolith-stub :8090, strangler-gateway :8080
./mvnw -pl course-service,monolith-stub,strangler-gateway -am spring-boot:run
```

Or run each module separately. Hit `GET http://localhost:8080/api/courses` repeatedly and watch responses come from either backend.

## Test

```bash
./mvnw -q test
```
