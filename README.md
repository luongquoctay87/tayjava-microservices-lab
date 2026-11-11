# TayJava Microservices Lab

Sample multi-module Spring Boot workspace that grows with the **TayJava Microservices** article series.

## This branch

| | |
|---|---|
| Branch | `lesson-23-schema-evolution` |
| Lesson | 23 — Event schema evolution |
| Parent | `lesson-22-saga` |
| What changed | **JSON Schema** (not Avro): `EnrollmentCreatedEvent` gains optional `schemaVersion` + `studentEmail` while keeping v1 payloads readable. Contract file: `common/src/main/resources/schemas/enrollment-created-v2.json`. Producers emit v2; consumers tolerate missing optional fields. |

## Run

```bash
docker compose up --build
```

## Test

```bash
./mvnw -q test
```

Demo users: `an.student` / `bao.teacher`; emails: `alice@example.com`, `bob@example.com`.
