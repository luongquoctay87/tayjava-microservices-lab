# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-28-zero-trust` |
| Lesson | 28 — Zero-trust between services (bridge) |
| Parent | `lesson-27-config-server` |
| What changed | Each API service validates identity itself via lab-only `Authorization: Bearer demo:<subject>` (`ZeroTrustAuthFilter` + registration). **`X-User-Id` / `X-Roles` are never credentials**. Course catalogue GET stays public; writes and Enrollment/Order/BFF require a Bearer. Deep JWT/OAuth2 → Spring Security series. |

## Demo auth (lab only)

```text
Authorization: Bearer demo:an.student
Authorization: Bearer demo:enrollment-service;scope=course.read   # lesson 29
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
