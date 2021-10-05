package com.infinum.assignment.domain.model.book

import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.interfaces.author.facade.dtos.AuthorDto
import com.infinum.assignment.tables.Author.AUTHOR
import com.infinum.assignment.tables.AuthorBook.AUTHOR_BOOK
import com.infinum.assignment.tables.Book.BOOK
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class JooqBookRepository(private val context: DSLContext) {

    fun findBooksByAuthorId(authorId: Long, pageable: Pageable): Page<Book> {
        val authorsBooks = context.select(
            BOOK.ISBN, BOOK.TITLE, BOOK.GENRE, multiset(
                select().from(AUTHOR).where(AUTHOR.ID.`in`(select(AUTHOR_BOOK.AUTHOR_ID).from(AUTHOR_BOOK).where(
                    AUTHOR_BOOK.BOOK_ISBN.eq(BOOK.ISBN))))
            ).`as`("authors").convertFrom { r ->
                r.map {
                    Author(
                        it.get("id") as Long,
                        it.get("first_name") as String,
                        it.get("last_name") as String,
                        (it.get("created_at") as OffsetDateTime).toLocalDateTime()
                    )
                }
            },
            BOOK.CREATED_AT
        )
            .from(AUTHOR_BOOK)
            .join(BOOK)
            .on(AUTHOR_BOOK.BOOK_ISBN.eq(BOOK.ISBN))
            .join(AUTHOR)
            .on(AUTHOR_BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .where(AUTHOR_BOOK.AUTHOR_ID.eq(authorId))
            .orderBy(BOOK.CREATED_AT.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize).fetchInto(Book::class.java)
        return PageImpl(
            authorsBooks,
            pageable,
            context.selectCount().from(AUTHOR_BOOK).where(AUTHOR_BOOK.AUTHOR_ID.eq(authorId))
                .fetchOne(0, Long::class.java) ?: 0
        )
    }

    fun findBookAuthors(bookIsbn: Long, sortType: String): List<AuthorDto> =
        context.select(
            AUTHOR.ID,
            AUTHOR.FIRST_NAME,
            AUTHOR.LAST_NAME,
            AUTHOR.CREATED_AT,
            field(selectCount().from(AUTHOR_BOOK).where(AUTHOR_BOOK.AUTHOR_ID.eq(AUTHOR.ID))).`as`("numOfBooksWritten")
        )
            .from(AUTHOR_BOOK)
            .join(AUTHOR)
            .on(AUTHOR_BOOK.AUTHOR_ID.eq(AUTHOR.ID))
            .where(AUTHOR_BOOK.BOOK_ISBN.eq(bookIsbn))
            .orderBy(
                when (sortType) {
                    "numOfBooks" -> inline(5).desc()
                    else -> AUTHOR.CREATED_AT.desc()
                }
            ).fetchInto(AuthorDto::class.java)

    fun findBookIsbnsCreatedInTheLastHour(): List<Long> =
        context.fetch("SELECT isbn FROM book WHERE created_at >= NOW() - INTERVAL '1 HOUR'").into(Long::class.java)

}