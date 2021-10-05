package com.infinum.assignment

import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.domain.model.author.AuthorRepository
import com.infinum.assignment.domain.model.book.BookRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.format.DateTimeFormatter

@SpringBootTest
@AutoConfigureMockMvc
class AuthorsBooksControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {

    @Test
    @DisplayName("should return all of authors books or not found if wrong author id")
    fun test1() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        val author3 =
            authorRepository.save(Author(firstName = TestData.author3.firstName, lastName = TestData.author3.lastName))
        bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        bookRepository.save(TestData.book2.toBook {
            TestData.book2.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        val book3 = bookRepository.save(TestData.book3.toBook {
            TestData.book3.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        val book4 = bookRepository.save(TestData.book4.toBook {
            TestData.book4.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        val book5 = bookRepository.save(TestData.book5.toBook {
            TestData.book5.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        mvc.get("/api/v1/authors/${author3.id}/books").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                    "_embedded": {
                                        "item": [
                                            {
                                            "isbn": ${book5.isbn},
                                            "title": "${book5.title}",
                                            "genre": "${book5.genre}",
                                            "createdAt": "${book5.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book5.authors[0].firstName}",
                                                    "lastName": "${book5.authors[0].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book5.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book5.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book5.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book5.title}"
                                                }
                                            }
                                        },
                                        {
                                            "isbn": ${book4.isbn},
                                            "title": "${book4.title}",
                                            "genre": "${book4.genre}",
                                            "createdAt": "${book4.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book4.authors[0].firstName}",
                                                    "lastName": "${book4.authors[0].lastName}"
                                                },
                                                {
                                                    "firstName": "${book4.authors[1].firstName}",
                                                    "lastName": "${book4.authors[1].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book4.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book4.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book4.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book4.title}"
                                                }
                                            }
                                        },
                                        {
                                            "isbn": ${book3.isbn},
                                            "title": "${book3.title}",
                                            "genre": "${book3.genre}",
                                            "createdAt": "${book3.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book3.authors[0].firstName}",
                                                    "lastName": "${book3.authors[0].lastName}"
                                                },
                                                {
                                                    "firstName": "${book3.authors[1].firstName}",
                                                    "lastName": "${book3.authors[1].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book3.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book3.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book3.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book3.title}"
                                                }
                                            }
                                        }
                                        ]
                                    },
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}/books?page=0&size=20"
                                        }
                                    },
                                    "page": {
                                        "size": 20,
                                        "totalElements": 3,
                                        "totalPages": 1,
                                        "number": 0
                                    }
                                }""", false
                )
            }
        }
        mvc.get("/api/v1/authors/523523/books").andExpect {
            status { isNotFound() }
            jsonPath("$.message") {
                value("Author with given id doesn't exist")
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

}