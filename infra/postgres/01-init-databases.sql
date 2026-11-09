-- Runs only while the PostgreSQL volume is empty. Passwords are demo values.
CREATE ROLE course_app LOGIN PASSWORD 'course_app_pw';
CREATE ROLE enrollment_app LOGIN PASSWORD 'enrollment_app_pw';

CREATE DATABASE course_db OWNER course_app;
CREATE DATABASE enrollment_db OWNER enrollment_app;

-- By default PUBLIC has CONNECT on every new database.
REVOKE CONNECT ON DATABASE course_db FROM PUBLIC;
REVOKE CONNECT ON DATABASE enrollment_db FROM PUBLIC;
