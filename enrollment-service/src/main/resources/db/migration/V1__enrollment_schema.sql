CREATE TABLE enrollments (
    id               BIGSERIAL PRIMARY KEY,
    student_username VARCHAR(100) NOT NULL,
    course_id        BIGINT NOT NULL,
    status           VARCHAR(40) NOT NULL
);

CREATE TABLE idempotency_records (
    id               BIGSERIAL PRIMARY KEY,
    scope            VARCHAR(40) NOT NULL,
    idempotency_key  VARCHAR(128) NOT NULL,
    request_hash     VARCHAR(64) NOT NULL,
    enrollment_id    BIGINT NOT NULL,
    CONSTRAINT uk_idempotency_scope_key UNIQUE (scope, idempotency_key)
);

CREATE TABLE outbox_events (
    id              BIGSERIAL PRIMARY KEY,
    event_id        VARCHAR(64) NOT NULL UNIQUE,
    aggregate_type  VARCHAR(64) NOT NULL,
    aggregate_id    VARCHAR(64) NOT NULL,
    event_type      VARCHAR(64) NOT NULL,
    payload         TEXT NOT NULL,
    status          VARCHAR(20) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL,
    published_at    TIMESTAMPTZ
);

CREATE TABLE saga_instances (
    id               BIGSERIAL PRIMARY KEY,
    idempotency_key  VARCHAR(100) NOT NULL UNIQUE,
    student_username VARCHAR(100) NOT NULL,
    student_email    VARCHAR(200) NOT NULL,
    course_id        BIGINT NOT NULL,
    status           VARCHAR(20) NOT NULL,
    current_step     VARCHAR(40) NOT NULL,
    order_ref        VARCHAR(64),
    payment_receipt  VARCHAR(64),
    enrollment_id    BIGINT,
    failure_reason   VARCHAR(500),
    created_at       TIMESTAMPTZ NOT NULL,
    updated_at       TIMESTAMPTZ NOT NULL
);
