package com.example.samazama.data

data class AuthorDetails(
    val name: String,
    val profile: String? = null,
    val awards: List<AuthorAward> = emptyList(),
    val books: BookSearchPage = BookSearchPage()
)

data class AuthorAward(
    val award: String,
    val year: Int? = null,
    val bookTitle: String? = null,
    val bookAsin: String? = null
)
