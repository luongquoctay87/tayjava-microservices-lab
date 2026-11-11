# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-07-database-per-service` |
| Lesson | 7 — PostgreSQL database per service + JPA |
| Parent | `lesson-06-compose` |
| What changed | One Postgres instance with `course_db` and `enrollment_db` (`ddl/`), Spring Data JPA repositories replace in-memory stores, Compose wires datasource env vars, H2 for tests. Services still do not call each other. |

| Service | Port | Database |
|---------|------|----------|
| `course-service` | 8081 | `course_db` |
| `enrollment-service` | 8082 | `enrollment_db` |

## DDL

See [`ddl/README.md`](ddl/README.md). Compose mounts `ddl/` into Postgres init on an empty volume. Demo passwords live in `ddl/01-init-databases.sql` and `.env.example` — not for production.

```bash
# re-run init scripts
docker compose down -v
docker compose up --build
```

## Run with Compose

```bash
docker compose up --build
```

Needs host ports `5432`, `8081`, and `8082` free. Discovery between containers uses Compose DNS (`postgres`, `course-service`, …) — not Eureka.

## Run from the host

Start Postgres via Compose (or apply `ddl/` manually), then:

```bash
./mvnw -N install
./mvnw -pl common install -DskipTests
./mvnw -pl course-service spring-boot:run
# other terminal
./mvnw -pl enrollment-service spring-boot:run
```

Requires **JDK 21**. Do not add `-am` to `spring-boot:run`.

## Test

```bash
./mvnw -q test
```

Tests use in-memory H2 and do not need PostgreSQL.

Demo users: `an.student` (student), `bao.teacher` (instructor). This lesson does not enforce roles.
