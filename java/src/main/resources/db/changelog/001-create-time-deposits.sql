--liquibase formatted sql

--changeset ikigai:001-create-time-deposits
CREATE SEQUENCE IF NOT EXISTS time_deposits_seq INCREMENT BY 50 START WITH 1;

CREATE TABLE time_deposits (
    id          INTEGER NOT NULL DEFAULT nextval('time_deposits_seq'),
    plan_type   VARCHAR(20) NOT NULL,
    days        INTEGER NOT NULL,
    balance     DOUBLE PRECISION NOT NULL,
    CONSTRAINT time_deposits_pkey PRIMARY KEY (id)
);

--rollback DROP TABLE time_deposits; DROP SEQUENCE time_deposits_seq;
