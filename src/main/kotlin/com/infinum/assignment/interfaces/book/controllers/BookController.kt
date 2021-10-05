package com.infinum.assignment.interfaces.book.controllers

import com.infinum.assignment.application.services.BookService
import com.infinum.assignment.infrastructure.exceptions.WrongAuthorSortTypeException
import com.infinum.assignment.interfaces.author.facade.assemblers.AuthorResourceAssembler
import com.infinum.assignment.interfaces.author.facade.resources.AuthorResource
import com.infinum.assignment.interfaces.book.facade.assemblers.BookResourceAssembler
import com.infinum.assignment.interfaces.book.facade.dtos.BookDto
import com.infinum.assignment.interfaces.book.facade.dtos.PublishBookDto
import com.infinum.assignment.interfaces.book.facade.resources.BookResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*
import javax.validation.Valid

@Controller
@RequestMapping("api/v1/books")
class BookController(
    private val bookService: BookService,
    private val bookResourceAssembler: BookResourceAssembler,
    private val authorResourceAssembler: AuthorResourceAssembler
) {

    @GetMapping("/isbn/{isbn}")
    fun getBookWithIsbn(@PathVariable("isbn") isbn: Long): ResponseEntity<BookResource> {
        return ResponseEntity.ok(bookResourceAssembler.toModel(bookService.findBookByIsbn(isbn)))
    }

    @PostMapping
    fun publishBook(@Valid @RequestBody publishBookDto: PublishBookDto): ResponseEntity<Unit> {
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/isbn/{isbn}")
                .buildAndExpand(bookService.publishBook(publishBookDto)).toUri()
        ).build()
    }

    @GetMapping("/title/{title}")
    fun getBooksWithTitle(
        @PathVariable("title") bookTitle: String,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<BookResource>> = ResponseEntity.ok(
        pagedResourcesAssembler.toModel(
            bookService.findBooksByTitle(
                bookTitle.trim().replace("%20", " ", true),
                pageable
            ),
            bookResourceAssembler
        )
    )

    @GetMapping("/genre/{genre}")
    fun getBooksWithGenre(
        @PathVariable("genre") bookGenre: String,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<BookResource>> = ResponseEntity.ok(
        pagedResourcesAssembler.toModel(
            bookService.findBooksByGenre(bookGenre, pageable),
            bookResourceAssembler
        )
    )

    @GetMapping
    fun getAllBooks(
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<BookDto>
    ): ResponseEntity<PagedModel<BookResource>> = ResponseEntity.ok(
        pagedResourcesAssembler.toModel(
            bookService.findAllBooks(pageable),
            bookResourceAssembler
        )
    )

    @GetMapping("/isbn/{isbn}/authors")
    fun getBookAuthors(
        @PathVariable("isbn") bookIsbn: Long,
        @RequestParam(name = "sortType") sortType: Optional<String>
    ): ResponseEntity<CollectionModel<AuthorResource>> =
        ResponseEntity.ok(
            authorResourceAssembler.toCollectionModel(
                bookService.findBookAuthors(
                    bookIsbn,
                    when (sortType.orElseGet { "" }) {
                        "numOfBooks" -> "numOfBooks"
                        "" -> ""
                        else -> throw WrongAuthorSortTypeException()
                    }
                )
            )
        )

}