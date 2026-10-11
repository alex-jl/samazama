package com.example.samazama.api

import com.example.samazama.data.BookDetails
import com.example.samazama.data.BookEdition
import com.example.samazama.data.BookReview
import com.example.samazama.data.BookReviewPage
import com.example.samazama.data.BookStore
import com.example.samazama.data.Reviewer
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException

const val MAX_REVIEWS_PER_PAGE = 40

fun bookUrl(id: Int): String = "$BOOKMETER_BASE_URL/books/$id"

fun bookReviewsUrl(id: Int, offset: Int = 0, limit: Int = MAX_REVIEWS_PER_PAGE): String =
    "${bookUrl(id)}/reviews.json?offset=$offset&limit=$limit"

fun bookStoresUrl(id: Int): String =
    "$BOOKMETER_BASE_URL/api/v1/books/$id/external_book_stores.json"

suspend fun fetchBookDetails(id: Int): BookDetails = coroutineScope {
    val reviews = async { fetchBookReviews(id) }
    // The store links are an extra, so the book is still shown when they can't be loaded.
    val stores = async {
        try {
            fetchBookStores(id)
        } catch (e: IOException) {
            emptyList()
        }
    }
    val url = bookUrl(id)
    parseBookDetails(id, fetchBookmeterPage(url), reviews.await(), stores.await())
        ?: throw IOException("GET $url returned a page without book details")
}

suspend fun fetchBookStores(id: Int): List<BookStore> {
    val url = bookStoresUrl(id)
    val json = fetchBookmeterPage(url)
    return try {
        parseBookStores(json)
    } catch (e: JSONException) {
        throw IOException("GET $url returned malformed stores", e)
    }
}

suspend fun fetchBookReviews(
    id: Int,
    offset: Int = 0,
    limit: Int = MAX_REVIEWS_PER_PAGE
): BookReviewPage {
    val url = bookReviewsUrl(id, offset, limit)
    val json = fetchBookmeterPage(url)
    return try {
        parseBookReviews(json)
    } catch (e: JSONException) {
        throw IOException("GET $url returned malformed reviews", e)
    }
}

fun parseBookDetails(
    id: Int,
    html: String,
    reviews: BookReviewPage = BookReviewPage(),
    stores: List<BookStore> = emptyList()
): BookDetails? {
    val document = Jsoup.parse(html)
    val title = document.selectFirst("h1.inner__title")?.text()
        ?.takeIf { it.isNotEmpty() } ?: return null
    val imageUrl = document.selectFirst(".group__image img[src]")?.attr("src")
        ?.takeIf { it.isNotEmpty() } ?: return null
    return BookDetails(
        id = id,
        title = title,
        author = document.authorNames(".header__authors"),
        imageUrl = imageUrl,
        format = document.labelledText(".current-book-detail__binding-name", FORMAT_LABEL),
        publisher = document.labelledText(".current-book-detail__publisher", PUBLISHER_LABEL),
        registrationCount = document.detailCount("登録数"),
        pageCount = document.detailCount("ページ数"),
        reviewCount = document.selectFirst(".supplement__value.count")?.int(),
        trialUrl = document.selectFirst("a.trial__btn[href]")?.attr("href")
            ?.takeIf { it.isNotEmpty() },
        editions = document.select("li.alternate-bindings-list__item").mapNotNull(::parseEdition),
        reviews = reviews,
        stores = stores
    )
}

fun parseBookStores(json: String): List<BookStore> {
    val resources = JSONObject(json).optJSONArray("resources")
    return (0 until (resources?.length() ?: 0))
        .mapNotNull { resources?.optJSONObject(it) }
        .sortedBy { it.optInt("priority", Int.MAX_VALUE) }
        .mapNotNull(::parseStore)
}

private fun parseStore(store: JSONObject): BookStore? {
    val name = store.optString("official_name").trim().takeIf { it.isNotEmpty() } ?: return null
    val url = store.optString("url").trim().takeIf { it.isNotEmpty() } ?: return null
    return BookStore(name = name, url = if (url.startsWith("//")) "https:$url" else url)
}

fun parseBookReviews(json: String): BookReviewPage {
    val root = JSONObject(json)
    val metadata = root.optJSONObject("metadata")
    val resources = root.optJSONArray("resources")
    val reviews = (0 until (resources?.length() ?: 0))
        .mapNotNull { resources?.optJSONObject(it)?.let(::parseReview) }
    return BookReviewPage(
        reviews = reviews,
        offset = metadata?.optInt("offset") ?: 0,
        totalCount = metadata?.optInt("count", reviews.size) ?: reviews.size
    )
}

private fun parseReview(review: JSONObject): BookReview? {
    val id = review.optInt("id").takeIf { it > 0 } ?: return null
    val user = review.optJSONObject("user") ?: return null
    return BookReview(
        id = id,
        content = review.optString("content").trim(),
        createdAt = review.optString("created_at"),
        reviewer = Reviewer(
            id = user.optInt("id"),
            name = user.optString("name"),
            imageUrl = user.optString("image")
        ),
        niceCount = review.optJSONObject("nice")?.optInt("count") ?: 0,
        commentCount = review.optJSONObject("comments")?.optJSONObject("metadata")
            ?.optInt("count") ?: 0,
        isSpoiler = review.optJSONObject("netabare")?.optBoolean("netabare") ?: false
    )
}

private fun parseEdition(item: Element): BookEdition? {
    val id = item.bookId() ?: return null
    return BookEdition(
        id = id,
        format = item.labelledText(".registration-and-binding__binding-name", FORMAT_LABEL),
        publisher = item.labelledText(
            ".alternate-bindings__link__content__publisher",
            PUBLISHER_LABEL
        ),
        registrationCount = item.selectFirst(".registration__counter")?.int()
    )
}

private fun Element.labelledText(selector: String, label: String): String? =
    selectFirst(selector)?.text()?.removePrefix(label)?.trim()?.takeIf { it.isNotEmpty() }

private fun Element.detailCount(title: String): Int? =
    selectFirst("dt.bm-details-side__title:containsOwn($title) + dd span")?.int()

private const val FORMAT_LABEL = "形式："
private const val PUBLISHER_LABEL = "出版社："
