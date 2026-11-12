# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-33-tracing` |
| Lesson | 33 — Distributed tracing with OpenTelemetry / Micrometer Tracing |
| Parent | `lesson-32-redis-cache` |
| What changed | `spring-boot-starter-opentelemetry` on Course, Enrollment, Order, BFF, Gateway; OTLP export off by default; Kafka observation enabled; `enrollment.enroll` Observation; optional Jaeger profile `observability`. |

## Tracing

| Setting | Default |
|---|---|
| `TRACING_EXPORT_ENABLED` | `false` (tests / local without collector) |
| `OTLP_TRACES_ENDPOINT` | `http://localhost:4318/v1/traces` |
| Sample probability | `1.0` in lab |

```bash
docker compose --profile observability up -d jaeger
TRACING_EXPORT_ENABLED=true docker compose up -d
```

Jaeger UI: http://localhost:16686

## Test

```bash
./mvnw -q test
```
