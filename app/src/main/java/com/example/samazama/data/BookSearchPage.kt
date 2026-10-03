package com.example.samazama.data

data class BookSearchPage(
    val books: List<Book>,
    val page: Int,
    val pageCount: Int,
    val totalCount: Int
) {
    val hasNextPage: Boolean get() = page < pageCount
}
