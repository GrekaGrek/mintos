CREATE TABLE IF NOT EXISTS customers (
    id       BIGSERIAL PRIMARY KEY NOT NULL,
    username VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS accounts (
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    customer_id BIGINT NOT NULL,
    currency    VARCHAR(3),
    balance     NUMERIC(6, 2),
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE IF NOT EXISTS transfers (
    id         BIGSERIAL PRIMARY KEY NOT NULL,
    source_account_id BIGINT,
    target_account_id BIGINT,
    amount     NUMERIC(6, 2),
    date_time  TIMESTAMP,
    FOREIGN KEY (source_account_id) REFERENCES accounts (id),
    FOREIGN KEY (target_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_accounts_customer_id ON accounts (customer_id);
CREATE INDEX idx_transfers_source_account_id ON transfers (source_account_id);
CREATE INDEX idx_transfers_target_account_id ON transfers (target_account_id);