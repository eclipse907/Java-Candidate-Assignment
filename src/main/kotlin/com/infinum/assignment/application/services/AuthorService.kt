package com.infinum.assignment.application.services

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.github.fge.jsonpatch.JsonPatch
import com.github.fge.jsonpatch.JsonPatchException
import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.domain.model.author.AuthorRepository
import com.infinum.assignment.domain.model.author.JooqAuthorRepository
import com.infinum.assignment.infrastructure.exceptions.WrongAuthorIdException
import com.infinum.assignment.infrastructure.exceptions.WrongAuthorPatchDataException
import com.infinum.assignment.interfaces.author.facade.dtos.AddAuthorDto
import com.infinum.assignment.interfaces.author.facade.dtos.AuthorDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
    private val jooqAuthorRepository: JooqAuthorRepository,
    private val objectMapper: ObjectMapper
) {

    fun getAuthorWithId(id: Long) = authorRepository.findById(id)?.let {
        AuthorDto(
            it.id,
            it.firstName,
            it.lastName,
            it.createdAt,
            jooqAuthorRepository.findByAuthorIdNumOfBooksAuthorWrote(id)
        )
    } ?: throw WrongAuthorIdException()

    fun addAuthor(addAuthorDto: AddAuthorDto): Long = authorRepository.save(addAuthorDto.toAuthor()).id

    fun applyPatchToAuthor(patch: JsonPatch, targetedAuthorId: Long): AuthorDto {
        try {
            val patched = patch.apply(
                objectMapper.convertValue(
                    authorRepository.findById(targetedAuthorId) ?: throw WrongAuthorIdException(), JsonNode::class.java
                )
            )
            return authorRepository.save(objectMapper.treeToValue<Author>(patched)!!).let {
                AuthorDto(
                    it.id,
                    it.firstName,
                    it.lastName,
                    it.createdAt,
                    jooqAuthorRepository.findByAuthorIdNumOfBooksAuthorWrote(it.id)
                )
            }
        } catch (ex: JsonPatchException) {
            throw WrongAuthorPatchDataException()
        }
    }

    fun getAllAuthors(sortType: String, pageable: Pageable): Page<AuthorDto> =
        jooqAuthorRepository.findAllAuthorsWithNumOfBooksWritten(sortType, pageable)

}