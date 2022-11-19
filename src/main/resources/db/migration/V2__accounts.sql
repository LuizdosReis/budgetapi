DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts
(
  id BIGSERIAL,
  name VARCHAR (50) NOT NULL,
  currency VARCHAR (3) NOT NULL,
  CONSTRAINT account_pk PRIMARY KEY (id)
);