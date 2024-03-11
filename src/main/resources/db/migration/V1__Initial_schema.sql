CREATE TABLE cardLists
(
    id           SERIAL PRIMARY KEY NOT NULL,
    name         VARCHAR(255),
    position     INTEGER,
    archived     BOOLEAN,
    created_date TIMESTAMP,
    board_id     BIGINT,
    creator_id   VARCHAR(255)
);

CREATE TABLE cards
(
    id           SERIAL PRIMARY KEY NOT NULL ,
    title        VARCHAR(255),
    description  TEXT,
    position     INTEGER,
    archived     BOOLEAN,
    created_date TIMESTAMP,
    cover_image  VARCHAR(255),
    cardlist_id  BIGINT,
    FOREIGN KEY (cardlist_id) REFERENCES cardlists (id)
);

CREATE TABLE attachments
(
    id           SERIAL PRIMARY KEY NOT NULL,
    card_id      BIGINT NOT NULL,
    user_id      VARCHAR(255),
    file_name    VARCHAR(255),
    content      BYTEA,
    archived     BOOLEAN,
    created_date TIMESTAMP,
    FOREIGN KEY (card_id) REFERENCES cards (id)
);




