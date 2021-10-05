package com.infinum.assignment

import com.infinum.assignment.interfaces.book.facade.dtos.PublishBookAuthorDto
import com.infinum.assignment.interfaces.book.facade.dtos.PublishBookDto

object TestData {

    val author1 = PublishBookAuthorDto("Marko", "Marulic")
    val author2 = PublishBookAuthorDto("Ivan", "Ivanko")
    val author3 = PublishBookAuthorDto("Goran", "Goranic")

    val book1 = PublishBookDto(
        9799100903038,
        "Book1",
        "Genre1",
        listOf(author1, author2)
    )
    val book2 = PublishBookDto(
        9796775874984,
        "Book2",
        "Genre1",
        listOf(author1)
    )
    val book3 = PublishBookDto(
        9798583295777,
        "Book3",
        "Genre2",
        listOf(author1, author3)
    )
    val book4 = PublishBookDto(
        9783909277889,
        "Book4",
        "Genre2",
        listOf(author2, author3)
    )
    val book5 = PublishBookDto(
        9788642142630,
        "Book5",
        "Genre3",
        listOf(author3)
    )

}