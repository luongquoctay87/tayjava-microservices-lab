# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-35-metrics` |
| Lesson | 35 — Metrics / SLO hooks (Micrometer) |
| Parent | `lesson-34-correlation-id` |
| What changed | Prometheus registry on services; `checkout.fulfill` timer + stuck gauge on order-service; `/actuator/prometheus`; scrape + alert **examples** under `observability/` (alerts = docs only). |

## Test

```bash
./mvnw -q test
```
