package com.infinum.assignment.infrastructure.exceptions

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import java.time.LocalDateTime

@JsonInclude(Include.NON_NULL)
data class ExceptionResponse(
    val message: String?,
    val detail: String?,
    val timestamp: LocalDateTime
)
