package com.example.samazama.api

import com.example.samazama.data.AuthorAward
import com.example.samazama.data.AuthorDetails
import com.example.samazama.data.BookSearchPage
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
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
    val document = Jsoup.parse(html)
    val name = document.selectFirst(".title__user")?.text()
        ?.takeIf { it.isNotEmpty() } ?: return null
    val sidebar = document.selectFirst(".books.search.sidebar")
    return AuthorDetails(
        name = name,
        profile = sidebar?.selectFirst(".group__detail")?.let(::parseProfile),
        awards = sidebar?.select(".list__awards li")?.mapNotNull(::parseAward).orEmpty(),
        books = books
    )
}

private fun parseProfile(detail: Element): String? {
    val paragraphs = detail.select("p").ifEmpty { listOf(detail) }
    return paragraphs.map { it.wholeText().trim() }.filter { it.isNotEmpty() }
        .joinToString("\n").takeIf { it.isNotEmpty() }
}

private fun parseAward(item: Element): AuthorAward? {
    val text = item.text()
    val match = AWARD_WITH_BOOK.find(text)
        ?: return text.takeIf { it.isNotEmpty() }?.let { AuthorAward(award = it) }
    val (year, book, award) = match.destructured
    return AuthorAward(
        award = award.trim(),
        year = year.toIntOrNull(),
        bookTitle = book.trim().takeIf { it.isNotEmpty() },
        bookAsin = item.selectFirst("a[href^=/b/]")?.attr("href")
            ?.let { AWARD_BOOK_PATH.find(it)?.groupValues?.get(1) }
    )
}

private val AUTHOR_PARAMETER = Regex("[?&]author=([^&#]*)")

private val AWARD_WITH_BOOK = Regex("^(\\d{4})?\\s*「(.*)」(.*)$")
private val AWARD_BOOK_PATH = Regex("^/b/([^/?#]+)")
