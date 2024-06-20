CREATE TABLE working_time
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

CREATE TABLE appointment
(
    id              UUID PRIMARY KEY,
    date            TIMESTAMP NOT NULL,
    is_patient_come BOOLEAN,
    service_info    VARCHAR(256),
    price           INT,
    patient_id      UUID      NOT NULL,
    doctor_id       UUID      NOT NULL
);

CREATE TABLE review
(
    id         UUID PRIMARY KEY,
    mark       SMALLINT     NOT NULL,
    title      VARCHAR(64)  NOT NULL,
    review     VARCHAR(256) NOT NULL,
    date       TIMESTAMP    NOT NULL,
    patient_id UUID         NOT NULL,
    doctor_id  UUID         NOT NULL
);