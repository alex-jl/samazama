package com.example.samazama.api

import com.example.samazama.data.AuthorAward
import com.example.samazama.data.AuthorDetails
import com.example.samazama.data.BookSearchPage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder

fun authorUrl(name: String): String =
    "$BOOKMETER_BASE_URL/search?author=${URLEncoder.encode(name, "UTF-8")}"

fun authorBooksUrl(name: String, page: Int = 1): String =
    "${authorUrl(name)}&partial=true&sort=recommended&page=$page"

fun authorNameFromUrl(url: String): String? =
    AUTHOR_PARAMETER.find(url)?.groupValues?.get(1)
        ?.let { URLDecoder.decode(it, "UTF-8").trim() }
        ?.takeIf { it.isNotEmpty() }

suspend fun fetchAuthorDetails(name: String): AuthorDetails = coroutineScope {
    val books = async { fetchAuthorBooks(name) }
    val url = authorUrl(name)
    parseAuthorDetails(fetchBookmeterPage(url), books.await())
        ?: throw IOException("GET $url returned a page without author details")
}

suspend fun fetchAuthorBooks(name: String, page: Int = 1): BookSearchPage =
    parseSearchPage(fetchBookmeterPage(authorBooksUrl(name, page)))

fun parseAuthorDetails(html: String, books: BookSearchPage = BookSearchPage()): AuthorDetails? {
    val name = AUTHOR_NAME.find(html)?.groupValues?.get(1)?.let(::plainText)
        ?.takeIf { it.isNotEmpty() } ?: return null
    val sidebar = html.substringAfter(AUTHOR_SIDEBAR_START, "")
    return AuthorDetails(
        name = name,
        profile = AUTHOR_PROFILE.find(sidebar)?.groupValues?.get(1)?.let(::plainText)
            ?.takeIf { it.isNotEmpty() },
        awards = AUTHOR_AWARD_LIST.find(sidebar)?.groupValues?.get(1)
            ?.let { list -> AWARD_ITEM.findAll(list).mapNotNull { parseAward(it.groupValues[1]) } }
            ?.toList().orEmpty(),
        books = books
    )
}

private fun parseAward(item: String): AuthorAward? {
    val match = AWARD_WITH_BOOK.find(item)
        ?: return plainText(item).takeIf { it.isNotEmpty() }?.let { AuthorAward(award = it) }
    val (year, book, award) = match.destructured
    return AuthorAward(
        award = plainText(award),
        year = year.toIntOrNull(),
        bookTitle = plainText(book).takeIf { it.isNotEmpty() },
        bookAsin = AWARD_BOOK_ASIN.find(book)?.groupValues?.get(1)
    )
}

private fun plainText(html: String): String =
    unescapeHtml(html.replace(PARAGRAPH_BREAK, "\n").replace(TAG, "")).trim()

private const val AUTHOR_SIDEBAR_START = "class=\"books search sidebar\""

private val AUTHOR_PARAMETER = Regex("[?&]author=([^&#]*)")
private val AUTHOR_NAME = Regex("class=\"title__user\">(.*?)</span>", RegexOption.DOT_MATCHES_ALL)
private val AUTHOR_PROFILE = Regex("class=\"group__detail\">(.*?)</div>", RegexOption.DOT_MATCHES_ALL)
private val AUTHOR_AWARD_LIST = Regex("class=\"list__awards\">(.*?)</ul>", RegexOption.DOT_MATCHES_ALL)
private val AWARD_ITEM = Regex("<li>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)

private val AWARD_WITH_BOOK = Regex("^\\s*(\\d{4})?\\s*「(.*)」(.*)$", RegexOption.DOT_MATCHES_ALL)
private val AWARD_BOOK_ASIN = Regex("href=\"/b/([^\"/?]+)\"")

private val PARAGRAPH_BREAK = Regex("</p>\\s*<p[^>]*>|<br\\s*/?>")
private val TAG = Regex("<[^>]*>")
