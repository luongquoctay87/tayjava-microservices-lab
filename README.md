# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-15-resilience4j` |
| Lesson | 15 — Resilience4j on outbound Course client |
| Parent | `lesson-14-grpc` |
| What changed | `resilience4j-spring-boot4` circuit breaker / retry / bulkhead on `ResilientCourseClient` wrapping `CourseTransport` (grpc\|rest\|web). Retries only `COURSE_UNAVAILABLE`; business `COURSE_NOT_ENROLLABLE` is not retried. Keeps `ddl/`. |

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
