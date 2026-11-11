# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-14-grpc` |
| Lesson | 14 — gRPC internal Course↔Enrollment |
| Parent | `lesson-13-restclient-webclient` |
| What changed | Shared `course.proto` in `common`; Course exposes gRPC `CourseInternal.GetCourse` on `:9091` while keeping public REST `/api/v1`. Enrollment defaults to `GrpcCourseClient` (`COURSE_TRANSPORT=grpc`); `rest` / `web` still switchable. Keeps `ddl/`. |

| Service | Port | Notes |
|---------|------|-------|
| `course-service` | 8081 REST / 9091 gRPC | `course_db` |
| `enrollment-service` | 8082 | `enrollment_db` |

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
