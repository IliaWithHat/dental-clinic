CREATE TABLE working_time
(
    id                       SERIAL PRIMARY KEY,
    day                      VARCHAR(10) NOT NULL,
    start_time               TIME        NOT NULL,
    end_time                 TIME        NOT NULL,
    time_interval_in_minutes INT         NOT NULL,
    doctor_id                INT         NOT NULL
);