--liquibase formatted sql

--changeset IliaWithHat:1
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