package com.infinum.assignment.infrastructure.exceptions

import java.lang.RuntimeException

open class WrongBookDataException(message: String) : RuntimeException(message)

class WrongBookIsbnLengthException : WrongBookDataException("Book ISBN length must be 13 digits")

class WrongBookIsbnEanPrefixException : WrongBookDataException("Book ISBN EAN Prefix is wrong")

class WrongBookIsbnCheckDigitException : WrongBookDataException("Book ISBN check digit is wrong")

class BookWithIsbnNotFoundException: RuntimeException("No book with given isbn found")

class BookWithTitleNotFoundException : RuntimeException("No book found with given title")

class BookAuthorWithGivenNameNotFoundException: WrongBookDataException("Author with given name doesn't exist")

open class WrongAuthorDataException(message: String) : RuntimeException(message)

class WrongAuthorIdException : WrongAuthorDataException("Author with given id doesn't exist")

class WrongAuthorPatchDataException : WrongAuthorDataException("Given author patch has wrong data")

class WrongAuthorSortTypeException: RuntimeException("Wrong author sort type in request")