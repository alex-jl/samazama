package dev.alexjl.samazama.data

data class BookPage(
    val books: List<Book> = emptyList(),
    val page: Int = 1,
    val pageCount: Int = 0,
    val totalCount: Int = 0
) {
    val hasNextPage: Boolean get() = page < pageCount
}
