CREATE INDEX IF NOT EXISTS idx_book_created_at ON book (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_author_created_at ON author (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_book_title ON book (title);
CREATE INDEX IF NOT EXISTS idx_book_genre ON book (genre);