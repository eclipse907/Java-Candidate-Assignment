package com.infinum.assignment.application.services

import com.infinum.assignment.domain.model.author.AuthorRepository
import com.infinum.assignment.domain.model.book.BookFactory
import com.infinum.assignment.domain.model.book.BookRepository
import com.infinum.assignment.domain.model.book.JooqBookRepository
import com.infinum.assignment.infrastructure.exceptions.BookWithIsbnNotFoundException
import com.infinum.assignment.infrastructure.exceptions.BookWithTitleNotFoundException
import com.infinum.assignment.infrastructure.exceptions.WrongAuthorIdException
import com.infinum.assignment.interfaces.author.facade.dtos.AuthorDto
import com.infinum.assignment.interfaces.book.facade.dtos.BookAuthorDto
import com.infinum.assignment.interfaces.book.facade.dtos.BookDto
import com.infinum.assignment.interfaces.book.facade.dtos.PublishBookDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val jooqBookRepository: JooqBookRepository,
    private val bookFactory: BookFactory,
    private val authorRepository: AuthorRepository
) {

    fun publishBook(publishBookDto: PublishBookDto): Long =
        bookRepository.save(bookFactory.createBook(publishBookDto)).isbn

    @Transactional
    fun findBookByIsbn(isbn: Long): BookDto = bookRepository.findByIsbn(isbn)?.let {
        BookDto(
            it.isbn,
            it.title,
            it.genre,
            it.createdAt,
            it.authors.map { author -> BookAuthorDto(author.firstName, author.lastName) }
        )
    } ?: throw BookWithIsbnNotFoundException()

    @Transactional
    fun findBooksByTitle(title: String, pageable: Pageable): Page<BookDto> {
        val foundBooks = bookRepository.findByTitleOrderByCreatedAtDesc(title, pageable)
        if (foundBooks.content.size <= 0) {
            throw BookWithTitleNotFoundException()
        }
        return foundBooks.map {
            BookDto(
                it.isbn,
                it.title,
                it.genre,
                it.createdAt,
                it.authors.map { author -> BookAuthorDto(author.firstName, author.lastName) }
            )
        }
    }


    @Transactional
    fun findBooksByGenre(genre: String, pageable: Pageable): Page<BookDto> =
        bookRepository.findByGenreOrderByCreatedAtDesc(genre, pageable).map {
            BookDto(
                it.isbn,
                it.title,
                it.genre,
                it.createdAt,
                it.authors.map { author -> BookAuthorDto(author.firstName, author.lastName) }
            )
        }

    @Transactional
    fun findBooksByAuthor(authorId: Long, pageable: Pageable): Page<BookDto> =
        authorRepository.findById(authorId)?.let { author ->
            jooqBookRepository.findBooksByAuthorId(author.id, pageable).map {
                BookDto(
                    it.isbn,
                    it.title,
                    it.genre,
                    it.createdAt,
                    it.authors.map { author -> BookAuthorDto(author.firstName, author.lastName) }
                )
            }
        } ?: throw WrongAuthorIdException()


    @Transactional
    fun findAllBooks(pageable: Pageable): Page<BookDto> = bookRepository.findAllByOrderByCreatedAtDesc(pageable).map {
        BookDto(
            it.isbn,
            it.title,
            it.genre,
            it.createdAt,
            it.authors.map { author -> BookAuthorDto(author.firstName, author.lastName) }
        )
    }

    @Transactional
    fun findBookAuthors(bookIsbn: Long, sortType: String): List<AuthorDto> =
        bookRepository.findByIsbn(bookIsbn)?.let {
            jooqBookRepository.findBookAuthors(it.isbn, sortType)
        } ?: throw BookWithIsbnNotFoundException()


    fun getIsbnsCreatedInTheLastHour(): List<Long> = jooqBookRepository.findBookIsbnsCreatedInTheLastHour()

}