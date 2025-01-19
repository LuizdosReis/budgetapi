DROP TABLE IF EXISTS categories;

CREATE TABLE categories
(
    id uuid,
    name VARCHAR (50) NOT NULL,
    type VARCHAR (7) NOT NULL,
    deleted BOOL NOT NULL DEFAULT false,
    create_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMPTZ,
    user_id uuid NOT NULL,
    CONSTRAINT categories_pk PRIMARY KEY (id),
    CONSTRAINT categories_fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT categories_types CHECK ( type IN ('INCOME', 'EXPENSE'))
);