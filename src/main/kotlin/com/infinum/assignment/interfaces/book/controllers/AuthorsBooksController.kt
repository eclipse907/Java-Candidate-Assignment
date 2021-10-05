package com.infinum.assignment.interfaces.book.controllers

import com.infinum.assignment.application.services.BookService
import com.infinum.assignment.interfaces.book.facade.assemblers.BookResourceAssembler
import com.infinum.assignment.interfaces.book.facade.dtos.BookDto
import com.infinum.assignment.interfaces.book.facade.resources.BookResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("api/v1/authors/{id}/books")
class AuthorsBooksController(
    private val bookService: BookService,
    private val bookResourceAssembler: BookResourceAssembler
) {

    @GetMapping
    fun getAllAuthorsBooks(
        @PathVariable("id") authorId: Long,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<BookResource>> = ResponseEntity.ok(
        pagedResourcesAssembler.toModel(bookService.findBooksByAuthor(authorId, pageable), bookResourceAssembler)
    )

}