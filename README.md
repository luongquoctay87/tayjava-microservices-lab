# tayjava-microservices

Hands-on workspace for the TayJava Microservices series. Each lesson that changes the running system lives on its own branch. Later branches keep everything from the earlier ones.

This branch is **lesson 6**: the lesson 5 services, packaged with a multi-stage Dockerfile per module and started together by Docker Compose. Data is still in memory. The services still do not call each other. Layout follows the TayJava backend: `controller`, `service`, `repository`, `entity`, and a shared `common` module for `Resp` / `ApiResp`.

| Service | Port | Owns |
|---------|------|------|
| `course-service` | 8081 | courses |
| `enrollment-service` | 8082 | enrollments (`courseId` only, not the course title) |

## Run with Compose

From the repository root:

```bash
docker compose up --build
```

The image build copies the `common` module because both services depend on it. Healthchecks call `/actuator/health` with `wget` inside the JRE Alpine image.

Stop the stack with `docker compose down`.

## Run from the host

Use two terminals. Do not add `-am` to `spring-boot:run`.

```bash
mvn -N install
mvn -pl common install -DskipTests
mvn -pl course-service spring-boot:run
mvn -pl enrollment-service spring-boot:run
```

`mvn -N install` publishes the parent POM. Without it, a later `spring-boot:run` cannot read `common`. Do not add `-am` to `spring-boot:run`: Maven would also run that goal on the parent POM and fail. Run the two services in separate terminals.

```bash
curl -s http://localhost:8081/api/courses
curl -s http://localhost:8081/actuator/health
curl -s -X POST http://localhost:8082/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"studentUsername":"an.student","courseId":1}'
curl -s 'http://localhost:8082/api/enrollments?student=an.student'
```

A successful call is wrapped as `{ "success": true, "code": "SUCCESS", "message": "...", "data": ... }`. A missing course returns HTTP 404 with `code` `COURSE_NOT_FOUND`.

Stopping `course-service` leaves `enrollment-service` running. That is process isolation, not a finished microservice. Compose, a database per service, and the HTTP call arrive in lessons 6–8.

Demo users in the article: `an.student` (student) and `bao.teacher` (instructor). This lesson does not enforce roles.
