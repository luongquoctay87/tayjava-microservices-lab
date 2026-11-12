CREATE TABLE course_orders (
    id               BIGSERIAL PRIMARY KEY,
    enrollment_id    BIGINT UNIQUE,
    student_username VARCHAR(100) NOT NULL,
    course_id        BIGINT NOT NULL,
    status           VARCHAR(40) NOT NULL,
    source_event_id  VARCHAR(64) NOT NULL,
    created_at       TIMESTAMPTZ NOT NULL,
    paid_at          TIMESTAMPTZ
);

CREATE TABLE inbox_events (
    event_id    VARCHAR(64) PRIMARY KEY,
    event_type  VARCHAR(64) NOT NULL,
    received_at TIMESTAMPTZ NOT NULL
);
