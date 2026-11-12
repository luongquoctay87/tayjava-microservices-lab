# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-32-redis-cache` |
| Lesson | 32 — Redis cache between Course and Enrollment |
| Parent | `lesson-30-eventual-consistency` |
| What changed | BFF `CourseCache` (cache-aside, jittered TTL, single-flight) for My Courses titles; Redis key `bff:course:v1:{id}`; course-service evicts the key after create/update commit; enroll write decisions still call Course directly. |

## Cache rules

| OK to cache | Not OK |
|---|---|
| Public title / slug / published for My Courses | Enroll / pay decisions |
| Shared key `bff:course:v1:{id}` | Per-user authorization under that key |

Redis failures are treated as misses. Stale titles are bounded by TTL (~120–150s) plus explicit eviction.

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
