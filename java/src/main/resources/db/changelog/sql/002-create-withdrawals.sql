--liquibase formatted sql

--changeset ikigai:002-create-withdrawals
CREATE SEQUENCE IF NOT EXISTS withdrawals_seq INCREMENT BY 50 START WITH 1;

CREATE TABLE withdrawals
(
    id              INTEGER          NOT NULL DEFAULT nextval('withdrawals_seq'),
    time_deposit_id INTEGER          NOT NULL,
    amount          DOUBLE PRECISION NOT NULL,
    date            DATE             NOT NULL,
    CONSTRAINT withdrawals_pkey PRIMARY KEY (id),
    CONSTRAINT withdrawals_time_deposit_id_fkey FOREIGN KEY (time_deposit_id)
        REFERENCES time_deposits (id)
);

--rollback DROP TABLE withdrawals; DROP SEQUENCE withdrawals_seq;
