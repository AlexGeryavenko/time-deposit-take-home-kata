--liquibase formatted sql

--changeset ikigai:003-add-indexes
CREATE INDEX withdrawals_time_deposit_id_idx ON withdrawals (time_deposit_id);

--rollback DROP INDEX withdrawals_time_deposit_id_idx;
