package com.infinum.assignment.domain.model.book

import com.infinum.assignment.domain.model.author.Author
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

@Entity
@Table(name = "book")
data class Book(
    @Id
    @Positive(message = "ISBN can't be negative or zero")
    val isbn: Long,

    @NotBlank(message = "Book title can't be blank")
    val title: String,

    @NotBlank(message = "Book genre can't be blank")
    val genre: String,

    @ManyToMany
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "book_isbn", referencedColumnName = "isbn")],
        inverseJoinColumns = [JoinColumn(name = "author_id", referencedColumnName = "id")]
    )
    val authors: List<Author>,

    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        return isbn == other.isbn
    }

    override fun hashCode() = javaClass.hashCode()

}