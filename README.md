# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-17-contract-testing` |
| Lesson | 17 — Spring Cloud Contract |
| Parent | `lesson-16-bff` |
| What changed | Provider contracts in `course-service` (`spring-cloud-contract-maven-plugin` + generated `CourseContractTest`). Consumer stub-runner test in `enrollment-service` (`CourseClientContractTest`) uses classpath stubs from the same contract. Runs under `./mvnw test`. Keeps `ddl/`. |

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
