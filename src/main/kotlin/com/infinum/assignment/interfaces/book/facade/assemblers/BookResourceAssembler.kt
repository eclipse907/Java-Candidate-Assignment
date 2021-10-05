package com.infinum.assignment.interfaces.book.facade.assemblers

import com.infinum.assignment.interfaces.annotations.ResourceAssembler
import com.infinum.assignment.interfaces.book.controllers.BookController
import com.infinum.assignment.interfaces.book.facade.dtos.BookDto
import com.infinum.assignment.interfaces.book.facade.resources.BookResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import java.time.format.DateTimeFormatter
import java.util.*

@ResourceAssembler
class BookResourceAssembler : RepresentationModelAssemblerSupport<BookDto, BookResource>(
    BookController::class.java,
    BookResource::class.java
) {

    private val noPagination = Pageable.unpaged()
    private val nullAssembler = PagedResourcesAssembler<BookDto>(null, null)

    override fun toModel(entity: BookDto): BookResource = instantiateModel(entity).apply {
        add(
            linkTo<BookController> {
                getBookWithIsbn(entity.isbn)
            }.withRel("self"),
            linkTo<BookController> {
                getBookAuthors(entity.isbn, Optional.empty())
            }.withRel("authors"),
            linkTo<BookController> {
                getBooksWithGenre(entity.genre, noPagination, nullAssembler)
            }.withRel("genre"),
            linkTo<BookController> {
                getBooksWithTitle(entity.title, noPagination, nullAssembler)
            }.withRel("title")
        )
    }

    override fun instantiateModel(entity: BookDto) = BookResource(
        entity.isbn,
        entity.title,
        entity.genre,
        entity.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
        entity.authors
    )

}