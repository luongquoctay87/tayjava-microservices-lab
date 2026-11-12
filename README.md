# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-30-eventual-consistency` |
| Lesson | 30 — Eventual consistency on Enrollment |
| Parent | `lesson-29-service-auth` |
| What changed | Checkout creates an Order without an enrollment row; `POST …/pay` returns **`FULFILLING`**. When Order consumes `EnrollmentCreated`, matching FULFILLING orders move to **`FULFILLED`**. BFF exposes **`GET /api/v1/checkout-status/{orderId}`** for polling and shows a My Courses banner for open FULFILLING orders (does not invent an `enrollmentId`). |

## Drift window

| Moment | Order | Enrollment | UI |
|---|---|---|---|
| After pay | `FULFILLING` | no row | “Order #… is being processed” |
| After Enrollment consumer | still `FULFILLING` until event reaches Order | `APPROVED` | My Courses may already list the course |
| After Order inbox | `FULFILLED` | `APPROVED` | checkout-status stops polling |

Poll `GET /api/v1/orders/{id}` or `GET /api/v1/checkout-status/{orderId}` with backoff — do not hammer My Courses every 50 ms looking for an enrollment that is intentionally missing.

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
