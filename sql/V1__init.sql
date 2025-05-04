CREATE TABLE mydb.users
(
    id           INTEGER PRIMARY KEY,
    name         VARCHAR(100)        NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    address      VARCHAR(255),
    created      TIMESTAMP,
    last_updated TIMESTAMP
);
