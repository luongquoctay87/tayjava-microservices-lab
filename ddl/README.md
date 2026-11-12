# DDL — database bootstrap

This folder creates **databases and roles** only (`01-init-databases.sql`).

**Schema source of truth** from lesson 37: Flyway migrations under each service:

- `course-service/src/main/resources/db/migration/`
- `enrollment-service/src/main/resources/db/migration/`
- `order-service/src/main/resources/db/migration/`

Compose still mounts this folder (or `infra/postgres/`) to Postgres initdb. Hibernate uses `ddl-auto: validate` against Flyway-managed tables.

```bash
docker compose up --build
```
