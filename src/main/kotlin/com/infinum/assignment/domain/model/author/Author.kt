package com.infinum.assignment.domain.model.author

import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "author")
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @NotBlank(message = "Authors first name can't be blank")
    val firstName: String,

    @NotBlank(message = "Authors last name can't be blank")
    val lastName: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Author

        return id > 0 && id == other.id
    }

    override fun hashCode() = javaClass.hashCode()
}