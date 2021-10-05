package com.infinum.assignment.domain.model.author

import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional

interface AuthorRepository : Repository<Author, Long> {

    fun save(author: Author): Author

    fun findById(id: Long): Author?

    fun findByFirstNameAndLastName(firstName: String, lastName: String): Author?

    @Transactional
    fun deleteAllBy()
}