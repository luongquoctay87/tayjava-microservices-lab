# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-34-correlation-id` |
| Lesson | 34 — Correlation ID from Gateway to services |
| Parent | `lesson-33-tracing` |
| What changed | Shared `CorrelationIds`; Gateway filter keeps/generates `X-Correlation-Id`; servlet filters + MDC on Course/Enrollment/Order/BFF; RestClient/WebClient propagation; Micrometer baggage; logging pattern includes `correlationId`. |

Correlation id ≠ OpenTelemetry trace id. Both appear in the log pattern.

## Test

```bash
./mvnw -q test
```
