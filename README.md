# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-25-gateway-routing` |
| Lesson | 25 — Routing, rewrite, and request aggregation at the Gateway |
| Parent | `lesson-24-gateway` |
| What changed | Gateway **RewritePath** (`/api/courses/**` → `/api/v1/courses/**`), **StripPrefix** for `/course-service/...`, ordered **SetPath** featured route to BFF, thin **`GET /api/catalog/summary`** aggregate with per-branch timeouts. BFF adds `/api/v1/featured-courses`. |

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
curl -s http://localhost:8080/api/courses
curl -s http://localhost:8080/api/catalog/summary
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
