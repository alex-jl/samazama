package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.BookSearchPage
import org.jsoup.Jsoup

enum class BookShelf(internal val urlSegment: String) {
    READ("read"),
    READING("reading"),
    STACKED("stacked"),
    WISH("wish"),
}

fun shelfUrl(userId: Int, shelf: BookShelf, page: Int = 1): String =
    "$BOOKMETER_BASE_URL/users/$userId/books/${shelf.urlSegment}?page=$page"

suspend fun fetchShelf(userId: Int, shelf: BookShelf, page: Int = 1): BookSearchPage =
    parseShelfPage(fetchBookmeterPage(shelfUrl(userId, shelf, page)))

fun parseShelfPage(html: String): BookSearchPage {
    val document = Jsoup.parse(html)
    val books = document.select("li.group__book")
        .mapNotNull { parseBook(it, preferCoverTitle = true) }
    val page = document.selectFirst(".bm-pagination li.active")?.int() ?: 1
    val lastLinkedPage = document.select(".bm-pagination a[href]")
        .mapNotNull { PAGE_QUERY.find(it.attr("href"))?.groupValues?.get(1)?.toIntOrNull() }
        .maxOrNull()
    val totalCount = document.selectFirst(".bm-pagination-notice")?.text()
        ?.let { TOTAL_COUNT.find(it)?.groupValues?.get(1) }
        ?.replace(",", "")?.toIntOrNull()
        ?: books.size
    return BookSearchPage(
        books = books,
        page = page,
        pageCount = if (totalCount == 0) 0 else maxOf(page, lastLinkedPage ?: 1),
        totalCount = totalCount
    )
}

private val PAGE_QUERY = Regex("[?&]page=(\\d+)")
private val TOTAL_COUNT = Regex("全([\\d,]+)件")
