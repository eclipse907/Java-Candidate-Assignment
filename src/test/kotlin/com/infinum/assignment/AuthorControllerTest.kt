package com.infinum.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import com.infinum.assignment.domain.model.author.Author
import com.infinum.assignment.domain.model.author.AuthorRepository
import com.infinum.assignment.domain.model.book.BookRepository
import com.infinum.assignment.interfaces.author.facade.resources.AuthorResource
import org.assertj.core.api.Assertions
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.format.DateTimeFormatter

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest @Autowired constructor(
    private val mvc: MockMvc,
    private val mapper: ObjectMapper,
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository
) {

    @Test
    @DisplayName("should throw 404 not found when bad author id")
    fun test1() {
        mvc.get("/api/v1/authors/34").andExpect {
            status { isNotFound() }
            jsonPath("$.message") {
                value("Author with given id doesn't exist")
            }
        }
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    @DisplayName("should add and then return author when good request")
    fun test2() {
        mvc.post("/api/v1/authors") {
            content = mapper.writeValueAsString(TestData.author1)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            header { stringValues("Location", "http://localhost/api/v1/authors/1") }
        }
        val author1 =
            authorRepository.findByFirstNameAndLastName(TestData.author1.firstName, TestData.author1.lastName)!!
        mvc.get("/api/v1/authors/1").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                                    "id": ${author1.id},
                                    "firstName": "${author1.firstName}",
                                    "lastName": "${author1.lastName}",
                                    "createdAt": "${author1.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                    "numOfBooksWritten": 0,
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/authors/${author1.id}"
                                        },
                                        "books": {
                                            "href": "http://localhost/api/v1/authors/${author1.id}/books"
                                        }
                                    }
                                  }""", false
                )
            }
        }
        authorRepository.deleteAllBy()
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    @DisplayName("should throw 4xx codes when bad post request")
    fun test3() {
        mvc.post("/api/v1/authors") {
            content = mapper.writeValueAsString(TestData.author1.copy(firstName = ""))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Bad post request arguments")
            }
        }
        mvc.post("/api/v1/authors") {
            content = mapper.writeValueAsString(TestData.author1.copy(lastName = ""))
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Bad post request arguments")
            }
        }
    }

    @Test
    @DisplayName("should return all authors or error if wrong author sort type")
    fun test4() {
        val author1 =
            authorRepository.save(Author(firstName = TestData.author1.firstName, lastName = TestData.author1.lastName))
        val author2 =
            authorRepository.save(Author(firstName = TestData.author2.firstName, lastName = TestData.author2.lastName))
        val author3 =
            authorRepository.save(Author(firstName = TestData.author3.firstName, lastName = TestData.author3.lastName))
        bookRepository.save(TestData.book1.toBook {
            TestData.book1.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        bookRepository.save(TestData.book2.toBook {
            TestData.book2.authors.map { authorRepository.findByFirstNameAndLastName(it.firstName, it.lastName)!! }
        })
        mvc.get("/api/v1/authors").andExpect {
            status { is2xxSuccessful() }
            content {
                json(
                    """{
                        "_embedded": {
                            "item": [
                                {
                                    "id": ${author3.id},
                                    "firstName": "${author3.firstName}",
                                    "lastName": "${author3.lastName}",
                                    "createdAt": "${author3.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                    "numOfBooksWritten": 0,
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}"
                                        },
                                        "books": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}/books"
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
                        },
                        "_links": {
                            "self": {
                                "href": "http://localhost/api/v1/authors?page=0&size=20"
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
        mvc.get("/api/v1/authors?sortType=numOfBooks").andExpect {
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
                                },
                                {
                                    "id": ${author3.id},
                                    "firstName": "${author3.firstName}",
                                    "lastName": "${author3.lastName}",
                                    "createdAt": "${author3.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                    "numOfBooksWritten": 0,
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}"
                                        },
                                        "books": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}/books"
                                        }
                                    }
                                }
                            ]
                        },
                        "_links": {
                            "self": {
                                "href": "http://localhost/api/v1/authors?sortType=numOfBooks&page=0&size=20"
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
        mvc.get("/api/v1/authors?sortType=bla").andExpect {
            status { isBadRequest() }
            jsonPath("$.message") {
                value("Wrong author sort type in request")
            }
        }
        authorRepository.deleteAllBy()
    }

    @Test
    @WithMockUser(authorities = ["SCOPE_ADMIN"])
    @DisplayName("should patch author or throw error if bad patch request")
    fun test5() {
        val author3 =
            authorRepository.save(Author(firstName = TestData.author3.firstName, lastName = TestData.author3.lastName))
        val result = mvc.perform(
            patch("/api/v1/authors/${author3.id}")
                .contentType(MediaType.valueOf("application/json-patch+json"))
                .content(
                    """
                [
                    {
                        "op": "replace",
                        "path": "/firstName",
                        "value": "Duro"
                    }
                ]
            """.trimIndent()
                )
        ).andExpect(status().isOk).andReturn()
        Assertions.assertThat(mapper.readValue(result.response.contentAsString, AuthorResource::class.java))
            .isEqualTo(
                mapper.readValue(
                    """
                                {
                                    "id": ${author3.id},
                                    "firstName": "Duro",
                                    "lastName": "${author3.lastName}",
                                    "createdAt": "${author3.createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))}",
                                    "numOfBooksWritten": 0,
                                    "_links": {
                                        "self": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}"
                                        },
                                        "books": {
                                            "href": "http://localhost/api/v1/authors/${author3.id}/books"
                                        }
                                    }
                                }
                """.trimIndent(), AuthorResource::class.java
                )
            )
        mvc.perform(
            patch("/api/v1/authors/${author3.id}")
                .contentType(MediaType.valueOf("application/json-patch+json"))
                .content(
                    """
                [
                    {
                        "op": "replace",
                        "path": "/first",
                        "value": "Bla"
                    }
                ]
            """.trimIndent()
                )
        ).andExpect(status().isBadRequest)
        mvc.perform(
            patch("/api/v1/authors/5353434535354")
                .contentType(MediaType.valueOf("application/json-patch+json"))
                .content(
                    """
                [
                    {
                        "op": "replace",
                        "path": "/first",
                        "value": "Bla"
                    }
                ]
            """.trimIndent()
                )
        ).andExpect(status().isNotFound)
    }

}