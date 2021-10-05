CREATE TABLE IF NOT EXISTS book
(
    isbn       BIGINT PRIMARY KEY,
    title      TEXT NOT NULL CHECK (title <> ''),
    genre      TEXT NOT NULL CHECK (genre <> ''),
    created_at TIMESTAMPTZ DEFAULT NOW()
);