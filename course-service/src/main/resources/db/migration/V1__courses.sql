CREATE TABLE courses (
    id                  BIGSERIAL PRIMARY KEY,
    slug                VARCHAR(100) NOT NULL UNIQUE,
    title               VARCHAR(255) NOT NULL,
    instructor_username VARCHAR(100) NOT NULL,
    published           BOOLEAN NOT NULL
);
