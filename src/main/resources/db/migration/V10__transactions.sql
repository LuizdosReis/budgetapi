DROP TABLE IF EXISTS transaction_tags;
DROP TABLE IF EXISTS transactions;

CREATE TABLE transactions
(
    id uuid,
    description VARCHAR (50) NOT NULL,
    deleted BOOL NOT NULL DEFAULT false,
    create_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMPTZ,
    account_id uuid NOT NULL,
    category_id uuid NOT NULL,
    amount NUMERIC(38, 2) NOT NULL,
    date DATE NOT NULL,
    status TEXT NOT NULL DEFAULT 'REGISTERED',
    CONSTRAINT transactions_pk PRIMARY KEY (id),
    CONSTRAINT transactions_fk_account FOREIGN KEY (account_id) REFERENCES accounts (id),
    CONSTRAINT transactions_fk_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT transactions_status CHECK (status IN ('REGISTERED', 'SCHEDULED', 'COMPLETED'))
);

CREATE TABLE transaction_tags
(
    transaction_id uuid NOT NULL,
    tag_id uuid NOT NULL,
    CONSTRAINT transaction_tags_fk_transactions FOREIGN KEY (transaction_id) REFERENCES transactions (id),
    CONSTRAINT transaction_tags_fk_tags FOREIGN KEY (tag_id) REFERENCES tags (id),
    CONSTRAINT transaction_tags_pk PRIMARY KEY (transaction_id, tag_id)
)