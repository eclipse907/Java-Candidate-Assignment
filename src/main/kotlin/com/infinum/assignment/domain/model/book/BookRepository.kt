package com.infinum.assignment.domain.model.book

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional

interface BookRepository : Repository<Book, Long> {

    fun save(book: Book): Book

    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Book>

    fun findByTitleOrderByCreatedAtDesc(title: String, pageable: Pageable): Page<Book>

    fun findByIsbn(isbn: Long): Book?

    fun findByGenreOrderByCreatedAtDesc(genre: String, pageable: Pageable): Page<Book>

    @Transactional
    fun deleteAllBy()

}