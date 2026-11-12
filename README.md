# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-27-config-server` |
| Lesson | 27 — Centralized configuration with Spring Cloud Config |
| Parent | `lesson-26-gateway-rate-limit` |
| What changed | New **`config-server`** module (native profile, port **8888**) reading `tayjava-config/` (env placeholders only — no real secrets). Enrollment imports `optional:configserver:` with `CONFIG_FAIL_FAST` (default false for local). Compose mounts the config dir and wires `CONFIG_SERVER_URI`. |

## Ports

| Service | Port |
|---|---|
| Gateway | 8080 |
| Course | 8081 |
| Enrollment | 8082 |
| BFF | 8083 |
| Order | 8084 |
| Config Server | 8888 (Compose: internal) |
| Redis | internal only |

## Run

```bash
docker compose up --build
curl -s http://localhost:8888/enrollment-service/docker   # if you publish 8888 locally
```

Local Config Server without Compose:

```bash
CONFIG_REPO_LOCATION=file:./tayjava-config ./mvnw -pl config-server spring-boot:run
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
