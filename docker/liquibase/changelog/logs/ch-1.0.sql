--liquibase formatted sql

--changeset IliaWithHat:1
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