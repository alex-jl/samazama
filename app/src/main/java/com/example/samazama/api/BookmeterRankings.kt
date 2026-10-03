package com.example.samazama.api

import com.example.samazama.data.Book

enum class RankingKind(internal val urlSegment: String) {
    READ_BOOK("read_book"),
    WISH_BOOK("wish_book"),
}

enum class BookFormat(internal val urlSegment: String) {
    BUNKO("bunko"),
    TANKOUBON("tankoubon"),
    COMIC("comic"),
    LIGHT_NOVEL("light_novel"),
    OTHERS("others"),
}

enum class RankingPeriod(internal val urlSegment: String) {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
}

fun rankingUrl(kind: RankingKind, format: BookFormat, period: RankingPeriod): String =
    "$BOOKMETER_BASE_URL/rankings/latest/${kind.urlSegment}/${format.urlSegment}/${period.urlSegment}"

suspend fun fetchRankings(
    kind: RankingKind = RankingKind.READ_BOOK,
    format: BookFormat = BookFormat.BUNKO,
    period: RankingPeriod = RankingPeriod.MONTH
): List<Book> = parseRankingBooks(fetchBookmeterPage(rankingUrl(kind, format, period)))

fun parseRankingBooks(html: String): List<Book> =
    html.split(BOOK_ITEM_START).drop(1).mapNotNull(::parseBook)

private const val BOOK_ITEM_START = "<li class=\"list__book\">"

private val BOOK_ID = Regex("/books/(\\d+)")
private val COVER_IMAGE = Regex("<img[^>]*\\ssrc=\"([^\"]*)\"")
private val TITLE = Regex("class=\"detail__title\">\\s*<a[^>]*>(.*?)</a>", RegexOption.DOT_MATCHES_ALL)
private val AUTHOR_LIST = Regex("class=\"detail__authors\">(.*?)</ul>", RegexOption.DOT_MATCHES_ALL)
private val REGISTRATION_COUNT =
    Regex("class=\"options__title\">登録</dt>\\s*<dd[^>]*>\\s*([\\d,]+)")
private val AUTHOR = Regex("<a[^>]*>(.*?)</a>", RegexOption.DOT_MATCHES_ALL)

private fun parseBook(item: String): Book? {
    val id = BOOK_ID.find(item)?.groupValues?.get(1)?.toIntOrNull() ?: return null
    val title = TITLE.find(item)?.groupValues?.get(1)?.let { unescapeHtml(it).trim() } ?: return null
    val imageUrl = COVER_IMAGE.find(item)?.groupValues?.get(1) ?: return null
    return Book(
        id = id,
        title = title,
        author = parseAuthors(item),
        imageUrl = unescapeHtml(imageUrl),
        registrationCount = REGISTRATION_COUNT.find(item)?.groupValues?.get(1)
            ?.replace(",", "")?.toIntOrNull()
    )
}

private fun parseAuthors(item: String): String {
    val authorList = AUTHOR_LIST.find(item)?.groupValues?.get(1) ?: return ""
    return AUTHOR.findAll(authorList)
        .map { unescapeHtml(it.groupValues[1]).trim() }
        .filter { it.isNotEmpty() }
        .joinToString(",")
}
