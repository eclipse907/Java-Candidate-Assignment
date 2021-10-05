package com.infinum.assignment.interfaces.author.controllers

import com.github.fge.jsonpatch.JsonPatch
import com.infinum.assignment.application.services.AuthorService
import com.infinum.assignment.infrastructure.exceptions.WrongAuthorSortTypeException
import com.infinum.assignment.interfaces.author.facade.assemblers.AuthorResourceAssembler
import com.infinum.assignment.interfaces.author.facade.dtos.AddAuthorDto
import com.infinum.assignment.interfaces.author.facade.dtos.AuthorDto
import com.infinum.assignment.interfaces.author.facade.resources.AuthorResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*
import javax.validation.Valid

@Controller
@RequestMapping("api/v1/authors")
class AuthorController(
    private val authorService: AuthorService,
    private val authorResourceAssembler: AuthorResourceAssembler
) {

    @GetMapping("/{id}")
    fun getAuthorWithId(@PathVariable("id") id: Long): ResponseEntity<AuthorResource> =
        ResponseEntity.ok(authorResourceAssembler.toModel(authorService.getAuthorWithId(id)))

    @PostMapping
    fun addAuthor(@Valid @RequestBody newAuthor: AddAuthorDto): ResponseEntity<Unit> {
        return ResponseEntity.created(
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(authorService.addAuthor(newAuthor)).toUri()
        ).build()
    }

    @PatchMapping("/{id}", consumes = ["application/json-patch+json"])
    fun updateAuthor(
        @PathVariable("id") id: Long,
        @RequestBody authorPatch: JsonPatch
    ): ResponseEntity<AuthorResource> {
        val updatedAuthor = authorService.applyPatchToAuthor(authorPatch, id)
        return ResponseEntity.ok(authorResourceAssembler.toModel(updatedAuthor))
    }

    @GetMapping
    fun getAllAuthors(
        @RequestParam(name = "sortType")
        sortType: Optional<String>,
        pageable: Pageable,
        pagedResourcesAssembler: PagedResourcesAssembler<AuthorDto>
    ): ResponseEntity<PagedModel<AuthorResource>> = ResponseEntity.ok(
        pagedResourcesAssembler.toModel(
            authorService.getAllAuthors(
                when (sortType.orElseGet { "" }) {
                    "numOfBooks" -> "numOfBooks"
                    "" -> ""
                    else -> throw WrongAuthorSortTypeException()
                },
                pageable
            ),
            authorResourceAssembler
        )
    )

}