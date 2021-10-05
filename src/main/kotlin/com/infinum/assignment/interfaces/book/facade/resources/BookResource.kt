package com.infinum.assignment.interfaces.book.facade.resources

import com.infinum.assignment.interfaces.book.facade.dtos.BookAuthorDto
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = IanaLinkRelations.ITEM_VALUE)
class BookResource(
    val isbn: Long,
    val title: String,
    val genre: String,
    val createdAt: String,
    val authors: List<BookAuthorDto>
) : RepresentationModel<BookResource>()
