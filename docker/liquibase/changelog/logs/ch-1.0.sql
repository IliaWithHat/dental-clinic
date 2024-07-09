--liquibase formatted sql

--changeset IliaWithHat:1
CREATE TABLE hello
(
    name TEXT
);

INSERT INTO hello
VALUES ('Hello World!!!');