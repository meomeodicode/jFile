CREATE TABLE IF NOT EXISTS users (
    uid BIGSERIAL PRIMARY KEY CHECK (uid > 0),
    username VARCHAR(256) UNIQUE
);

CREATE TABLE IF NOT EXISTS users_passwords (
    of_uid BIGSERIAL PRIMARY KEY,
    hash VARCHAR(128),
    FOREIGN KEY (of_uid) REFERENCES users(uid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS download_tasks (
    download_id VARCHAR(256),
    download_of_uid BIGSERIAL,
    download_type SMALLINT,
    url VARCHAR(256),
    download_status SMALLINT,
    metadata TEXT,
    FOREIGN KEY (download_of_uid) REFERENCES users(uid) ON DELETE CASCADE
);