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
    val title = DETAILS_TITLE.findText(html) ?: return null
    val imageUrl = DETAILS_COVER.findText(html) ?: return null
    return BookDetails(
        id = id,
        title = title,
        author = DETAILS_AUTHOR_LIST.find(html)?.groupValues?.get(1)?.let(::joinAuthorLinks).orEmpty(),
        imageUrl = imageUrl,
        format = DETAILS_FORMAT.findText(html),
        publisher = DETAILS_PUBLISHER.findText(html),
        registrationCount = DETAILS_REGISTRATION_COUNT.findInt(html),
        pageCount = DETAILS_PAGE_COUNT.findInt(html),
        reviewCount = DETAILS_REVIEW_COUNT.findInt(html),
        trialUrl = DETAILS_TRIAL_URL.findText(html),
        editions = html.split(EDITION_ITEM_START).drop(1)
            .mapNotNull { parseEdition(it.substringBefore("</li>")) },
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

private fun parseEdition(item: String): BookEdition? {
    val id = EDITION_ID.findInt(item) ?: return null
    return BookEdition(
        id = id,
        format = EDITION_FORMAT.findText(item),
        publisher = EDITION_PUBLISHER.findText(item),
        registrationCount = EDITION_REGISTRATION_COUNT.findInt(item)
    )
}

private fun Regex.findText(text: String): String? =
    find(text)?.groupValues?.get(1)?.let { unescapeHtml(it).trim() }?.takeIf { it.isNotEmpty() }

private fun Regex.findInt(text: String): Int? =
    find(text)?.groupValues?.get(1)?.replace(",", "")?.toIntOrNull()

private const val EDITION_ITEM_START = "<li class=\"alternate-bindings-list__item\">"

private val DETAILS_TITLE = Regex("class=\"inner__title\">(.*?)</h1>", RegexOption.DOT_MATCHES_ALL)
private val DETAILS_AUTHOR_LIST =
    Regex("class=\"header__authors\">(.*?)</ul>", RegexOption.DOT_MATCHES_ALL)
private val DETAILS_COVER =
    Regex("class=\"group__image\">.*?<img[^>]*\\ssrc=\"([^\"]*)\"", RegexOption.DOT_MATCHES_ALL)
private val DETAILS_FORMAT = Regex("class=\"current-book-detail__binding-name\">形式：([^<]*)<")
private val DETAILS_PUBLISHER = Regex("class=\"current-book-detail__publisher\">出版社：([^<]*)<")
private val DETAILS_REGISTRATION_COUNT = Regex(">登録数</dt>\\s*<dd[^>]*>\\s*<span[^>]*>\\s*([\\d,]+)")
private val DETAILS_PAGE_COUNT = Regex(">ページ数</dt>\\s*<dd[^>]*>\\s*<span[^>]*>\\s*([\\d,]+)")
private val DETAILS_REVIEW_COUNT = Regex("class=\"supplement__value count\">\\s*([\\d,]+)")
private val DETAILS_TRIAL_URL = Regex("class=\"trial__btn\"[^>]*\\shref=\"([^\"]*)\"")

private val EDITION_ID = Regex("href=\"/books/(\\d+)\"")
private val EDITION_FORMAT = Regex("class=\"registration-and-binding__binding-name\">形式：([^<]*)<")
private val EDITION_PUBLISHER =
    Regex("class=\"alternate-bindings__link__content__publisher\">出版社：([^<]*)<")
private val EDITION_REGISTRATION_COUNT = Regex("class=\"registration__counter\">\\s*([\\d,]+)")
