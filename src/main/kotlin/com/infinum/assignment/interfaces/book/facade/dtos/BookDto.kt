package com.infinum.assignment.interfaces.book.facade.dtos

import java.time.LocalDateTime

data class BookDto(
    val isbn: Long,
    val title: String,
    val genre: String,
    val createdAt: LocalDateTime,
    val authors: List<BookAuthorDto>
)
