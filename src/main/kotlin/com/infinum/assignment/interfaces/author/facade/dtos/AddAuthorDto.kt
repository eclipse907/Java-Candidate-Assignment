package com.infinum.assignment.interfaces.author.facade.dtos

import com.infinum.assignment.domain.model.author.Author
import javax.validation.constraints.NotBlank

data class AddAuthorDto(
    @field:NotBlank(message = "Authors first name can't be blank")
    val firstName: String,

    @field:NotBlank(message = "Authors last name can't be blank")
    val lastName: String
) {
    fun toAuthor() = Author(
        firstName = firstName,
        lastName = lastName
    )
}
