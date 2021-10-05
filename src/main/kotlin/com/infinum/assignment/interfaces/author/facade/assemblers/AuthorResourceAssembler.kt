package com.infinum.assignment.interfaces.author.facade.assemblers

import com.infinum.assignment.interfaces.annotations.ResourceAssembler
import com.infinum.assignment.interfaces.author.controllers.AuthorController
import com.infinum.assignment.interfaces.author.facade.dtos.AuthorDto
import com.infinum.assignment.interfaces.author.facade.resources.AuthorResource
import com.infinum.assignment.interfaces.book.controllers.AuthorsBooksController
import com.infinum.assignment.interfaces.book.facade.dtos.BookDto
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import org.springframework.hateoas.server.mvc.linkTo
import java.time.format.DateTimeFormatter

@ResourceAssembler
class AuthorResourceAssembler : RepresentationModelAssemblerSupport<AuthorDto, AuthorResource>(
    AuthorController::class.java,
    AuthorResource::class.java
) {

    private val noPagination = Pageable.unpaged()
    private val nullAssembler = PagedResourcesAssembler<BookDto>(null, null)

    override fun toModel(entity: AuthorDto): AuthorResource = createModelWithId(entity.id, entity).apply {
        add(linkTo<AuthorsBooksController> {
            getAllAuthorsBooks(entity.id, noPagination, nullAssembler)
        }.withRel("books"))
    }

    override fun instantiateModel(entity: AuthorDto) = AuthorResource(
        entity.id,
        entity.firstName,
        entity.lastName,
        entity.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
        entity.numOfBooksWritten
    )

}