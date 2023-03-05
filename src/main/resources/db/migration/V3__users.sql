DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id uuid NOT NULL,
    username VARCHAR (50) NOT NULL UNIQUE,
    password VARCHAR NOT NULL,
    roles VARCHAR (50) NOT NULL,
    CONSTRAINT users_pk PRIMARY KEY (id)
);