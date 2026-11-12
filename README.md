# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-36-health-shutdown` |
| Lesson | 36 — Readiness/liveness split and graceful shutdown |
| Parent | `lesson-35-metrics` |
| What changed | `server.shutdown=graceful` + lifecycle timeout; Actuator probes; readiness includes `db` (and enrollment `outboxBacklog`); Compose healthchecks hit `/actuator/health/readiness` with `stop_grace_period: 40s`. |

Liveness stays process-focused. Dependency/backlog pain belongs on readiness so orchestrators stop sending traffic without killing a healthy JVM.

## Test

```bash
./mvnw -q test
```
