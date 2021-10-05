CREATE TABLE IF NOT EXISTS author
(
    id         BIGSERIAL PRIMARY KEY,
    first_name TEXT NOT NULL CHECK (first_name <> ''),
    last_name  TEXT NOT NULL CHECK (last_name <> ''),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE (first_name, last_name)
);