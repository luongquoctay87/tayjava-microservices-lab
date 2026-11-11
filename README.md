# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

Readers run **course-service** and **enrollment-service** as separate processes while learning distributed concerns step by step. Each lesson that changes the running system lives on its own git branch. Later branches keep everything from the earlier ones.

This repository is a companion for learning — not the TayJava production backend.

## What you will build

- Two Spring Boot services under package `com.tayjava` (`controller` / `service` / `repository` / `entity`) plus a shared `common` module
- Shared API envelope (`Resp` / `ApiResp`) and errors (`ErrorException` / `ErrorCode`) aligned with `e-learning`
- In-memory stores first; later branches add Compose, PostgreSQL per service, HTTP calls between services, and more

## Tech stack

| Area | Choice |
|------|--------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.9 |
| Web | Spring MVC (`spring-boot-starter-webmvc`) |
| Build | Maven Wrapper (`./mvnw`) |
| Persistence (later) | Spring Data JPA, PostgreSQL |
| Tests | JUnit 5, MockMvc |

## This branch

| | |
|---|---|
| Branch | `master` |
| Lesson | Base — multi-module Course + Enrollment services (in-memory) |
| Parent | — |
| What changed | Parent POM `tayjava-microservices-lab`, `common` envelope/errors, two services with TayJava layering, Maven Wrapper, smoke tests |

## Run

Requires **JDK 21**.

```bash
./mvnw -N install
./mvnw -pl common install -DskipTests
./mvnw -pl course-service spring-boot:run
# other terminal
./mvnw -pl enrollment-service spring-boot:run
```

| Service | Port |
|---------|------|
| `course-service` | `8081` |
| `enrollment-service` | `8082` |

Do not add `-am` to `spring-boot:run` on a service module.

## Test

```bash
./mvnw -q test
```
