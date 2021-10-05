package com.infinum.assignment.interfaces.author.facade.resources

import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation

@Relation(collectionRelation = IanaLinkRelations.ITEM_VALUE)
data class AuthorResource(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val createdAt: String,
    val numOfBooksWritten: Long
) : RepresentationModel<AuthorResource>()
