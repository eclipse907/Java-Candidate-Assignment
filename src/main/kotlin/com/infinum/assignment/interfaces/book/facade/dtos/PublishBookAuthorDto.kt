package com.infinum.assignment.interfaces.book.facade.dtos

import javax.validation.constraints.NotBlank

data class PublishBookAuthorDto(
    @field:NotBlank(message = "Authors first name can't be blank")
    val firstName: String,

    @field:NotBlank(message = "Authors last name can't be blank")
    val lastName: String
)