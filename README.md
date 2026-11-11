# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-20-outbox` |
| Lesson | 20 — Transactional outbox |
| Parent | `lesson-19-kafka` |
| What changed | Enrollment writes `outbox_events` in the same DB transaction as the enrollment row; `OutboxRelay` polls PENDING rows and publishes `EnrollmentCreatedEvent` to Kafka. Keeps Compose Kafka + `order-service` consumer. Unit tests disable the broker and use a recording sender. |

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
