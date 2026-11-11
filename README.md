# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-24-gateway` |
| Lesson | 24 — API Gateway with Spring Cloud Gateway |
| Parent | `lesson-23-schema-evolution` |
| What changed | New module **`gateway-service`** (Spring Cloud Gateway WebFlux, Cloud BOM `2025.1.4`). Path routes for Course `/api/v1/courses/**`, Enrollment `/api/v1/enrollments/**` + `/api/v1/purchases/**`, Order `/api/v1/orders/**`, BFF `/api/v1/me/**`. Edge CORS, connect/response timeouts, `X-Correlation-Id` global filter, Compose service on port **8080**. No Eureka. |

## Ports

| Service | Port |
|---|---|
| Gateway | 8080 |
| Course | 8081 |
| Enrollment | 8082 |
| BFF | 8083 |
| Order | 8084 |

## Run

```bash
docker compose up --build
curl -s http://localhost:8080/api/v1/courses
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
