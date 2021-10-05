package com.infinum.assignment.interfaces.book.facade.dtos

import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.domain.model.book.Book
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Positive

data class PublishBookDto(
    @field:Positive(message = "ISBN can't be negative or zero")
    val isbn: Long,

    @field:NotBlank(message = "Book title can't be blank")
    val title: String,

    @field:NotBlank(message = "Book genre can't be blank")
    val genre: String,

    @field:NotEmpty(message = "Book authors can't be empty or null")
    val authors: List<PublishBookAuthorDto>
) {
    fun toBook(authorsFetcher: (List<PublishBookAuthorDto>) -> List<Author>) = Book(
        isbn,
        title,
        genre,
        authorsFetcher.invoke(authors)
    )
}
