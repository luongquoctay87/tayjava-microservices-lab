# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-21-inbox` |
| Lesson | 21 — Inbox / consumer dedup |
| Parent | `lesson-20-outbox` |
| What changed | `order-service` stores processed Kafka `event_id` in `inbox_events` before creating an order. Duplicate deliveries are skipped (at-least-once safe). Keeps outbox publisher and Compose Kafka. |

## Run

```bash
docker compose up --build
```

Ports: Course `8081`, Enrollment `8082`, BFF `8083`, Order `8084`, Kafka `9092`.

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
