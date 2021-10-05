package com.infinum.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.domain.model.author.AuthorRepository
import com.infinum.assignment.domain.model.book.BookRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.time.format.DateTimeFormatter


@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {

    @Test
    @DisplayName("should throw 404 not found when bad book isbn")
    fun test1() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        mvc.get("/api/v1/books/isbn/123456789").andExpect {
            status { isNotFound() }
            jsonPath("$.message") {
                value("No book with given isbn found")
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_AUTHOR"])
    @DisplayName("should create and return book")
    fun test2() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        mvc.post("/api/v1/books") {
            content = mapper.writeValueAsString(TestData.book1)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            header { stringValues("Location", "http://localhost/api/v1/books/isbn/${TestData.book1.isbn}") }
        }
        mvc.get("/api/v1/books/isbn/${TestData.book1.isbn}").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                    "isbn": ${TestData.book1.isbn},
                                    "title": "${TestData.book1.title}",
                                    "genre": "${TestData.book1.genre}",
                                    "createdAt": "${
                        bookRepository.findByIsbn(TestData.book1.isbn)?.createdAt?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                    }",
                                    "authors": [
                                        {
                                            "firstName": "${TestData.book1.authors[0].firstName}",
                                            "lastName": "${TestData.book1.authors[0].lastName}"
                                        },
                                        {
                                            "firstName": "${TestData.book1.authors[1].firstName}",
                                            "lastName": "${TestData.book1.authors[1].lastName}"
                                        }
                                    ],
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/books/isbn/${TestData.book1.isbn}"
                                        },
                                        "authors": {
                                            "href": "http://localhost/api/v1/books/isbn/${TestData.book1.isbn}/authors{?sortType}"
                                        },
                                        "genre": {
                                            "href": "http://localhost/api/v1/books/genre/${TestData.book1.genre}"
                                        },
                                        "title": {
                                            "href": "http://localhost/api/v1/books/title/${TestData.book1.title}"
                                        }
                                    }
                                   }""", false
                )
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_AUTHOR"])
    @DisplayName("should return bad request for wrong post data")
    fun test3() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        mvc.post("/api/v1/books") {
            content = mapper.writeValueAsString(TestData.book1.copy(isbn = -1234567890123))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Bad post request arguments")
            }
        }
        mvc.post("/api/v1/books") {
            content = mapper.writeValueAsString(TestData.book1.copy(title = ""))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Bad post request arguments")
            }
        }
        mvc.post("/api/v1/books") {
            content = mapper.writeValueAsString(TestData.book1.copy(genre = ""))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Bad post request arguments")
            }
        }
        mvc.post("/api/v1/books") {
            content = mapper.writeValueAsString(TestData.book1.copy(authors = listOf()))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Bad post request arguments")
            }
        }
        mvc.post("/api/v1/books") {
            content =
                mapper.writeValueAsString(TestData.book1.copy(authors = TestData.book1.authors.map { it.copy(firstName = "") }))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Author with given name doesn't exist")
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

    @Test
    @DisplayName("should return book with title or not found")
    fun test4() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        authorRepository.save(Author(firstName = TestData.author3.firstName, lastName = TestData.author3.lastName))
        val book1 = bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        bookRepository.save(TestData.book2.toBook {
            TestData.book2.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        mvc.get("/api/v1/books/title/${book1.title}").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                "_embedded": {
                                    "item": [
                                        {
                                            "isbn": ${book1.isbn},
                                            "title": "${book1.title}",
                                            "genre": "${book1.genre}",
                                            "createdAt": "${book1.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book1.authors[0].firstName}",
                                                    "lastName": "${book1.authors[0].lastName}"
                                                },
                                                {
                                                    "firstName": "${book1.authors[1].firstName}",
                                                    "lastName": "${book1.authors[1].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book1.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book1.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book1.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book1.title}"
                                                }
                                            }
                                        }
                                    ]
                                },
                                "_links": {
                                    "self": {
                                        "href": "http://localhost/api/v1/books/title/${book1.title}?page=0&size=20"
                                    }
                                },
                                "page": {
                                    "size": 20,
                                    "totalElements": 1,
                                    "totalPages": 1,
                                    "number": 0
                                }
                            }""", false
                )
            }
        }
        mvc.get("/api/v1/books/title/bla").andExpect {
            status { isNotFound() }
            jsonPath("$.message") {
                value("No book found with given title")
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

    @Test
    @DisplayName("should return books with the given genre or empty response if no books with given genre")
    fun test5() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        authorRepository.save(Author(firstName = TestData.author3.firstName, lastName = TestData.author3.lastName))
        val book1 = bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        val book2 = bookRepository.save(TestData.book2.toBook {
            TestData.book2.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        bookRepository.save(TestData.book3.toBook {
            TestData.book3.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        mvc.get("/api/v1/books/genre/${book1.genre}").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                "_embedded": {
                                    "item": [
                                        {
                                            "isbn": ${book2.isbn},
                                            "title": "${book2.title}",
                                            "genre": "${book2.genre}",
                                            "createdAt": "${book2.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book2.authors[0].firstName}",
                                                    "lastName": "${book2.authors[0].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book2.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book2.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book2.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book2.title}"
                                                }
                                            }
                                        },
                                        {
                                            "isbn": ${book1.isbn},
                                            "title": "${book1.title}",
                                            "genre": "${book1.genre}",
                                            "createdAt": "${book1.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book1.authors[0].firstName}",
                                                    "lastName": "${book1.authors[0].lastName}"
                                                },
                                                {
                                                    "firstName": "${book1.authors[1].firstName}",
                                                    "lastName": "${book1.authors[1].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book1.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book1.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book1.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book1.title}"
                                                }
                                            }
                                        }
                                    ]
                                },
                                "_links": {
                                    "self": {
                                        "href": "http://localhost/api/v1/books/genre/${book1.genre}?page=0&size=20"
                                    }
                                },
                                "page": {
                                    "size": 20,
                                    "totalElements": 2,
                                    "totalPages": 1,
                                    "number": 0
                                }
                    }""", false
                )
            }
        }
        mvc.get("/api/v1/books/genre/bla").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                "_links": {
                                    "self": {
                                        "href": "http://localhost/api/v1/books/genre/bla?page=0&size=20"
                                    }
                                },
                                "page": {
                                    "size": 20,
                                    "totalElements": 0,
                                    "totalPages": 0,
                                    "number": 0
                                }
                    }""", false
                )
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

    @Test
    @DisplayName("should return all books")
    fun test6() {
        authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        authorRepository.save(Author(firstName = TestData.author3.firstName, lastName = TestData.author3.lastName))
        val book1 = bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        val book2 = bookRepository.save(TestData.book2.toBook {
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
        mvc.get("/api/v1/books").andExpect {
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
                                        },
                                        {
                                            "isbn": ${book2.isbn},
                                            "title": "${book2.title}",
                                            "genre": "${book2.genre}",
                                            "createdAt": "${book2.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book2.authors[0].firstName}",
                                                    "lastName": "${book2.authors[0].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book2.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book2.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book2.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book2.title}"
                                                }
                                            }
                                        },
                                        {
                                            "isbn": ${book1.isbn},
                                            "title": "${book1.title}",
                                            "genre": "${book1.genre}",
                                            "createdAt": "${book1.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                            "authors": [
                                                {
                                                    "firstName": "${book1.authors[0].firstName}",
                                                    "lastName": "${book1.authors[0].lastName}"
                                                },
                                                {
                                                    "firstName": "${book1.authors[1].firstName}",
                                                    "lastName": "${book1.authors[1].lastName}"
                                                }
                                            ],
                                            "_links": {
                                                "self": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book1.isbn}"
                                                },
                                                "authors": {
                                                    "href": "http://localhost/api/v1/books/isbn/${book1.isbn}/authors{?sortType}"
                                                },
                                                "genre": {
                                                    "href": "http://localhost/api/v1/books/genre/${book1.genre}"
                                                },
                                                "title": {
                                                    "href": "http://localhost/api/v1/books/title/${book1.title}"
                                                }
                                            }
                                        }
                                        ]
                                    },
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/books?page=0&size=20"
                                        }
                                    },
                                    "page": {
                                        "size": 20,
                                        "totalElements": 5,
                                        "totalPages": 1,
                                        "number": 0
                                    }
                           }""", false
                )
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

    @Test
    @DisplayName("should return books authors or error if bad request")
    fun test7() {
        val author1 =
            authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        val author2 =
            authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        val book1 = bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        bookRepository.save(TestData.book2.toBook {
            TestData.book2.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        mvc.get("/api/v1/books/isbn/${book1.isbn}/authors").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                    "_embedded": {
                                        "item": [
                                            {
                                                "id": ${author2.id},
                                                "firstName": "${author2.firstName}",
                                                "lastName": "${author2.lastName}",
                                                "createdAt": "${author2.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                                "numOfBooksWritten": 1,
                                                "_links": {
                                                    "self": {
                                                        "href": "http://localhost/api/v1/authors/${author2.id}"
                                                    },
                                                    "books": {
                                                        "href": "http://localhost/api/v1/authors/${author2.id}/books"
                                                    }
                                                }
                                            },
                                            {
                                                "id": ${author1.id},
                                                "firstName": "${author1.firstName}",
                                                "lastName": "${author1.lastName}",
                                                "createdAt": "${author1.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                                "numOfBooksWritten": 2,
                                                "_links": {
                                                    "self": {
                                                        "href": "http://localhost/api/v1/authors/${author1.id}"
                                                    },
                                                    "books": {
                                                        "href": "http://localhost/api/v1/authors/${author1.id}/books"
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                }""", false
                )
            }
        }
        mvc.get("/api/v1/books/isbn/${book1.isbn}/authors?sortType=numOfBooks").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                    "_embedded": {
                                        "item": [
                                            {
                                                "id": ${author1.id},
                                                "firstName": "${author1.firstName}",
                                                "lastName": "${author1.lastName}",
                                                "createdAt": "${author1.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                                "numOfBooksWritten": 2,
                                                "_links": {
                                                    "self": {
                                                        "href": "http://localhost/api/v1/authors/${author1.id}"
                                                    },
                                                    "books": {
                                                        "href": "http://localhost/api/v1/authors/${author1.id}/books"
                                                    }
                                                }
                                            },
                                            {
                                                "id": ${author2.id},
                                                "firstName": "${author2.firstName}",
                                                "lastName": "${author2.lastName}",
                                                "createdAt": "${author2.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                                "numOfBooksWritten": 1,
                                                "_links": {
                                                    "self": {
                                                        "href": "http://localhost/api/v1/authors/${author2.id}"
                                                    },
                                                    "books": {
                                                        "href": "http://localhost/api/v1/authors/${author2.id}/books"
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                }""", false
                )
            }
        }
        mvc.get("/api/v1/books/isbn/1234567891234/authors").andExpect {
            status { isNotFound() }
            jsonPath("$.message") {
                value("No book with given isbn found")
            }
        }
        mvc.get("/api/v1/books/isbn/${book1.isbn}/authors?sortType=bla").andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Wrong author sort type in request")
            }
        }
        bookRepository.deleteAllBy()
        authorRepository.deleteAllBy()
    }

}