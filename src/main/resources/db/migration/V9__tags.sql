DROP TABLE IF EXISTS tags;

CREATE TABLE tags
(
    id uuid,
    name VARCHAR (50) NOT NULL,
    deleted BOOL NOT NULL DEFAULT false,
    create_date TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMPTZ,
    user_id uuid NOT NULL,
    CONSTRAINT tags_pk PRIMARY KEY (id),
    CONSTRAINT tags_fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT tags_uk UNIQUE (name, user_id)
);