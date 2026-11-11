# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-22-saga` |
| Lesson | 22 — Saga (course purchase) |
| Parent | `lesson-21-inbox` |
| What changed | **Orchestration** saga for purchasing a course (`POST /api/v1/purchases`): validate course → create order intent → charge payment → confirm enrollment (writes outbox). Compensates on failure (refund + cancel order). Chosen style: **orchestration** (central `PurchaseSagaOrchestrator`), not choreography. Demo decline: `bob@example.com`. |

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
