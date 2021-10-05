package com.infinum.assignment.domain.model.book

import com.infinum.assignment.domain.annotations.Factory
import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.domain.model.author.AuthorRepository
import com.infinum.assignment.infrastructure.exceptions.*
import com.infinum.assignment.interfaces.book.facade.dtos.PublishBookAuthorDto
import com.infinum.assignment.interfaces.book.facade.dtos.PublishBookDto

@Factory
class BookFactory(
    private val authorRepository: AuthorRepository
) {

    fun createBook(publishBookDto: PublishBookDto): Book {
        val isbnDigits = mutableListOf<Int>()
        var temp = publishBookDto.isbn
        while (temp > 0) {
            isbnDigits.add((temp % 10).toInt())
            temp /= 10
        }
        if (isbnDigits.size != 13) {
            throw WrongBookIsbnLengthException()
        }
        isbnDigits.reverse()
        if (isbnDigits[0] != 9 || isbnDigits[1] != 7 || !(isbnDigits[2] == 8 || isbnDigits[2] == 9)) {
            throw WrongBookIsbnEanPrefixException()
        }
        var sum = 0
        for (i in 1 until 13) {
            sum += isbnDigits[i - 1] * if (i % 2 == 0) {
                3
            } else {
                1
            }
        }
        val module = sum % 10
        if (module == 0 && isbnDigits[12] != 0 || module != 0 && isbnDigits[12] != 10 - module) {
            throw WrongBookIsbnCheckDigitException()
        }
        return publishBookDto.toBook(fun(authorsDtos: List<PublishBookAuthorDto>): List<Author> {
            val authors = mutableListOf<Author>()
            for (authorData in authorsDtos) {
                val author = authorRepository.findByFirstNameAndLastName(authorData.firstName, authorData.lastName)
                    ?: throw BookAuthorWithGivenNameNotFoundException()
                authors.add(author)
            }
            return authors
        })
    }

}