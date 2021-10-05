CREATE TABLE IF NOT EXISTS author_book
(
    author_id BIGINT NOT NULL,
    book_isbn   BIGINT NOT NULL,
    PRIMARY KEY (author_id, book_isbn),
    FOREIGN KEY (author_id) REFERENCES author(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (book_isbn) REFERENCES book (isbn) ON UPDATE CASCADE ON DELETE CASCADE
);