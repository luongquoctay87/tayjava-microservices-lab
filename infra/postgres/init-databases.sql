-- Demo roles and databases for database-per-service (lesson 7+).
-- Applied automatically by Compose on an empty Postgres volume via /docker-entrypoint-initdb.d.
-- Passwords below are demo values; override with env / .env for anything beyond local learning.

CREATE ROLE course_app LOGIN PASSWORD 'course_app_pw';
CREATE ROLE enrollment_app LOGIN PASSWORD 'enrollment_app_pw';
CREATE ROLE order_app LOGIN PASSWORD 'order_app_pw';

CREATE DATABASE course_db OWNER course_app;
CREATE DATABASE enrollment_db OWNER enrollment_app;
CREATE DATABASE order_db OWNER order_app;

REVOKE CONNECT ON DATABASE course_db FROM PUBLIC;
REVOKE CONNECT ON DATABASE enrollment_db FROM PUBLIC;
REVOKE CONNECT ON DATABASE order_db FROM PUBLIC;
