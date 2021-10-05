package com.infinum.assignment.domain.model.author

import com.infinum.assignment.interfaces.author.facade.dtos.AuthorDto
import com.infinum.assignment.tables.Author.AUTHOR
import com.infinum.assignment.tables.AuthorBook.AUTHOR_BOOK
import org.jooq.DSLContext
import org.jooq.impl.DSL.count
import org.jooq.impl.DSL.inline
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class JooqAuthorRepository(private val context: DSLContext) {

    fun findAllAuthorsWithNumOfBooksWritten(sortType: String, pageable: Pageable): Page<AuthorDto> {
        val authors = context.select(
            AUTHOR.ID,
            AUTHOR.FIRST_NAME,
            AUTHOR.LAST_NAME,
            AUTHOR.CREATED_AT,
            count(AUTHOR_BOOK.BOOK_ISBN).`as`("numOfBooksWritten")
        )
            .from(AUTHOR)
            .leftJoin(AUTHOR_BOOK)
            .on(AUTHOR.ID.eq(AUTHOR_BOOK.AUTHOR_ID))
            .groupBy(AUTHOR.ID)
            .orderBy(
                when (sortType) {
                    "numOfBooks" -> inline(5).desc()
                    else -> AUTHOR.CREATED_AT.desc()
                }
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize)
            .fetchInto(AuthorDto::class.java)
        return PageImpl(
            authors,
            pageable,
            context.selectCount().from(AUTHOR).fetchOne(0, Long::class.java) ?: 0
        )
    }

    fun findByAuthorIdNumOfBooksAuthorWrote(id: Long): Long = context.selectCount()
        .from(AUTHOR_BOOK)
        .where(AUTHOR_BOOK.AUTHOR_ID.eq(id))
        .fetchOne(0, Long::class.java) ?: 0

}