# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-37-compose-baseline` |
| Lesson | 37 — Compose baseline TayJava + Flyway SoT |
| Parent | `lesson-36-health-shutdown` |
| What changed | Flyway migrations are the schema source of truth (`ddl-auto: validate`); Postgres init lives under `infra/postgres/`; Compose health/grace baseline kept; temporary demo comments cleaned. |

## Schema

| Service | Migrations |
|---|---|
| course-service | `src/main/resources/db/migration/` |
| enrollment-service | `src/main/resources/db/migration/` |
| order-service | `src/main/resources/db/migration/` |

Tests keep `spring.flyway.enabled=false` + H2 `create-drop` for deterministic unit/slice tests.

## Test

```bash
./mvnw -q test
```

## Run (Compose)

```bash
cp .env.example .env
docker compose up --build
```

Optional observability: `docker compose --profile observability up -d`
