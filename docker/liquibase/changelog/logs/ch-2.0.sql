--liquibase formatted sql

--changeset IliaWithHat:1
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