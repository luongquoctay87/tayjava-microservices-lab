# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-19-kafka` |
| Lesson | 19 — Kafka messaging |
| Parent | `lesson-17-contract-testing` |
| What changed | Kafka (KRaft) in Compose; `enrollment-service` publishes `EnrollmentCreatedEvent` to topic `enrollment.events`; new `order-service` (:8084) consumes and creates a course order stub. Shared event DTO in `common`. Keeps `ddl/` (adds `order_db`). Unit tests run with `tayjava.kafka.enabled=false` (no live broker required). |

## Run

```bash
docker compose up --build
```

Ports: Course `8081`, Enrollment `8082`, BFF `8083`, Order `8084`, Kafka `9092`.

Demo flow: enroll via Enrollment → event on `enrollment.events` → Order creates a row → `GET /api/v1/orders?student=alice@example.com`.

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
