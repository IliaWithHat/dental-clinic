CREATE SCHEMA time_service_schema;
CREATE SCHEMA appointment_service_schema;
CREATE SCHEMA review_service_schema;


CREATE USER time_user WITH PASSWORD 'password';
CREATE USER appointment_user WITH PASSWORD 'password';
CREATE USER review_user WITH PASSWORD 'password';


GRANT USAGE ON SCHEMA time_service_schema TO time_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA time_service_schema TO time_user;

GRANT USAGE ON SCHEMA appointment_service_schema TO appointment_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA appointment_service_schema TO appointment_user;

GRANT USAGE ON SCHEMA review_service_schema TO review_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA review_service_schema TO review_user;


CREATE TABLE time_service_schema.working_time
(
    id                       UUID PRIMARY KEY,
    day                      VARCHAR(10) NOT NULL,
    start_time               TIME        NOT NULL,
    end_time                 TIME        NOT NULL,
    break_start_time         TIME,
    break_end_time           TIME,
    time_interval_in_minutes INT         NOT NULL,
    doctor_id                UUID        NOT NULL
);

CREATE TABLE appointment_service_schema.appointment
(
    id              UUID PRIMARY KEY,
    date            TIMESTAMP NOT NULL,
    is_patient_come BOOLEAN,
    service_info    VARCHAR(256),
    price           INT,
    patient_id      UUID      NOT NULL,
    doctor_id       UUID      NOT NULL
);

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