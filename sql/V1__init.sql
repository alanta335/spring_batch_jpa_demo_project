CREATE TABLE mydb.users
(
    id           INTEGER PRIMARY KEY,
    name         VARCHAR(100),
    email        VARCHAR(100) UNIQUE,
    address      VARCHAR(255),
    created      TIMESTAMP,
    last_updated TIMESTAMP
);
