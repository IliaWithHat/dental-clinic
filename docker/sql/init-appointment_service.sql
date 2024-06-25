CREATE SCHEMA appointment_service_schema;

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

CREATE USER appointment_user WITH PASSWORD 'password';

GRANT USAGE ON SCHEMA appointment_service_schema TO appointment_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA appointment_service_schema TO appointment_user;