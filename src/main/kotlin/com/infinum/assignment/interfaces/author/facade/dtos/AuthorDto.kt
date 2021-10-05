package com.infinum.assignment.interfaces.author.facade.dtos

import java.time.LocalDateTime

data class AuthorDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val createdAt: LocalDateTime,
    val numOfBooksWritten: Long
)
