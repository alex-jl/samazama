package com.example.samazama.api

import com.example.samazama.data.BookSearchPage
import java.net.URLEncoder

fun searchUrl(keyword: String, page: Int = 1): String =
    "$BOOKMETER_BASE_URL/search?keyword=${URLEncoder.encode(keyword, "UTF-8")}" +
            "&partial=true&sort=recommended&page=$page"

suspend fun searchBooks(keyword: String, page: Int = 1): BookSearchPage =
    parseSearchPage(fetchBookmeterPage(searchUrl(keyword, page)))

fun parseSearchPage(html: String): BookSearchPage {
    val books = html.split(SEARCH_ITEM_START).drop(1)
        .mapNotNull { parseBook(it, preferCoverTitle = true) }
    val pagination = PAGINATION.find(html)?.groupValues?.get(1)?.let(::unescapeHtml).orEmpty()
    val offset = paginationValue(pagination, "offset") ?: 0
    val perPage = paginationValue(pagination, "per_page")?.takeIf { it > 0 }
    val totalCount = paginationValue(pagination, "total") ?: books.size
    return BookSearchPage(
        books = books,
        page = if (perPage != null) offset / perPage + 1 else 1,
        pageCount = when {
            perPage != null -> (totalCount + perPage - 1) / perPage
            books.isEmpty() -> 0
            else -> 1
        },
        totalCount = totalCount
    )
}

private fun paginationValue(pagination: String, key: String): Int? =
    Regex("\"$key\":(\\d+)").find(pagination)?.groupValues?.get(1)?.toIntOrNull()

private const val SEARCH_ITEM_START = "<li class=\"group__book\">"

/** JSON such as `{"pagination":{"offset":0,"per_page":20,"total":51}}`, HTML-escaped. */
private val PAGINATION = Regex("data-pagination=\"([^\"]*)\"")
