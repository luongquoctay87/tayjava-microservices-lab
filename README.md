# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-16-bff` |
| Lesson | 16 — BFF composition |
| Parent | `lesson-15-resilience4j` |
| What changed | New module `bff-service` (:8083) exposes `GET /api/v1/me/courses?student=` by composing Enrollment + Course over HTTP. Keeps prior services and `ddl/`. |

| Service | Port |
|---------|------|
| `course-service` | 8081 / gRPC 9091 |
| `enrollment-service` | 8082 |
| `bff-service` | 8083 |

## Test

```bash
./mvnw -q test
```

```bash
curl -s 'http://localhost:8083/api/v1/me/courses?student=an.student' -H 'API-Version: 1'
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
