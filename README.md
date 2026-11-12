# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-29-service-auth` |
| Lesson | 29 — Service-to-service auth (bridge) |
| Parent | `lesson-28-zero-trust` |
| What changed | Course exposes **`GET /internal/courses/{id}`** (not Gateway-routed) requiring demo Bearer with `scope=course.read`. Enrollment REST/WebClient attach a lab client-credentials stand-in (`Bearer demo:enrollment-service;scope=course.read`) and call the internal path. **mTLS** is documented only (optional Compose SSL bundles belong in a later hardening pass / Spring Security series). |

## Demo tokens

```text
# User (lesson 28)
Authorization: Bearer demo:an.student

# Service client-credentials stand-in (this lesson)
Authorization: Bearer demo:enrollment-service;scope=course.read
```

## mTLS (optional, not wired)

Mutual TLS authenticates the channel once SANs are checked. This lab keeps HTTP plaintext on the Compose network and proves caller identity with the scoped demo Bearer above. Production often combines mesh mTLS + token scopes.

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
