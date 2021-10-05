package com.infinum.assignment.infrastructure.exceptions

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.Exception
import java.time.LocalDateTime

@ControllerAdvice
class ControllerAdvisor : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handleExceptions(ex: Exception) = ResponseEntity.internalServerError().body(
        ExceptionResponse(ex.message, null, LocalDateTime.now())
    )

    @ExceptionHandler(BookWithIsbnNotFoundException::class)
    fun handleBookWithIsbnNotFoundException(ex: BookWithIsbnNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse(ex.message, null, LocalDateTime.now()))

    @ExceptionHandler(BookWithTitleNotFoundException::class)
    fun handleBookWithTitleNotFoundException(ex: BookWithTitleNotFoundException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse(ex.message, null, LocalDateTime.now()))

    @ExceptionHandler(WrongBookDataException::class)
    fun handleWrongBookDataException(ex: WrongBookDataException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse(ex.message, null, LocalDateTime.now()))

    @ExceptionHandler(WrongAuthorDataException::class)
    fun handleWrongAuthorDataException(ex: WrongAuthorDataException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse(ex.message, null, LocalDateTime.now()))

    @ExceptionHandler(WrongAuthorIdException::class)
    fun handleWrongAuthorIdException(ex: WrongAuthorIdException) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse(ex.message, null, LocalDateTime.now()))

    @ExceptionHandler(WrongAuthorSortTypeException::class)
    fun handleWrongAuthorSortTypeException(ex: WrongAuthorSortTypeException) =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ExceptionResponse(ex.message, null, LocalDateTime.now()))

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> = ResponseEntity.status(status).body(
        ExceptionResponse("Bad post request arguments", ex.bindingResult.toString(), LocalDateTime.now())
    )

}