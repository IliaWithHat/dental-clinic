CREATE SCHEMA review_service_schema;

CREATE TABLE review_service_schema.review
(
    id         UUID PRIMARY KEY,
    mark       SMALLINT     NOT NULL,
    title      VARCHAR(64)  NOT NULL,
    review     VARCHAR(256) NOT NULL,
    date       TIMESTAMP    NOT NULL,
    patient_id UUID         NOT NULL,
    doctor_id  UUID         NOT NULL
);

CREATE USER review_user WITH PASSWORD 'password';

GRANT USAGE ON SCHEMA review_service_schema TO review_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA review_service_schema TO review_user;