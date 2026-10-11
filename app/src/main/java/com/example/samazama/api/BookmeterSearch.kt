package com.example.samazama.api

import com.example.samazama.data.BookSearchPage
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder

fun searchUrl(keyword: String, page: Int = 1): String =
    "$BOOKMETER_BASE_URL/search?keyword=${URLEncoder.encode(keyword, "UTF-8")}" +
            "&partial=true&sort=recommended&page=$page"

suspend fun searchBooks(keyword: String, page: Int = 1): BookSearchPage =
    parseSearchPage(fetchBookmeterPage(searchUrl(keyword, page)))

fun parseSearchPage(html: String): BookSearchPage {
    val document = Jsoup.parse(html)
    val books = document.select("li.group__book")
        .mapNotNull { parseBook(it, preferCoverTitle = true) }
    val pagination = document.selectFirst("[data-pagination]")?.attr("data-pagination")
        ?.let(::parsePagination)
    val offset = pagination?.optInt("offset") ?: 0
    val perPage = pagination?.optInt("per_page")?.takeIf { it > 0 }
    val totalCount = pagination?.takeIf { it.has("total") }?.optInt("total") ?: books.size
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

/** JSON such as `{"pagination":{"offset":0,"per_page":20,"total":51}}`. */
private fun parsePagination(json: String): JSONObject? =
    try {
        JSONObject(json).optJSONObject("pagination")
    } catch (e: JSONException) {
        null
    }
