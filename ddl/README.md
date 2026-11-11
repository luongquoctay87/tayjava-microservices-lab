# DDL — database per service

Each service owns its own PostgreSQL database. There is no shared schema and no cross-database foreign key.

| Database | Owner role | Used by |
|----------|------------|---------|
| `course_db` | `course_app` | `course-service` |
| `enrollment_db` | `enrollment_app` | `enrollment-service` |

## Apply with Compose (recommended)

`compose.yaml` mounts this folder to `/docker-entrypoint-initdb.d`. Scripts run **once** when the Postgres volume is empty:

```bash
docker compose up --build
```

To re-run init scripts, remove the volume:

```bash
docker compose down -v
docker compose up --build
```

## Apply manually (host Postgres)

```bash
psql -h 127.0.0.1 -U postgres -f ddl/01-init-databases.sql
```

Then point each service at its database with `SPRING_DATASOURCE_*` (see root README / `.env.example`).

Tables are created by Hibernate (`ddl-auto: update`) on first start. Flyway becomes the source of truth in a later lesson.
