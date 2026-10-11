package com.example.samazama.api

import com.example.samazama.data.Book
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

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
    Jsoup.parse(html).select("li.list__book").mapNotNull { parseBook(it) }

internal fun parseBook(item: Element, preferCoverTitle: Boolean = false): Book? {
    val id = item.bookId() ?: return null
    val cover = item.selectFirst("img[src]") ?: return null
    val imageUrl = cover.attr("src").takeIf { it.isNotEmpty() } ?: return null
    val coverTitle = if (preferCoverTitle) cover.attr("alt").trim() else ""
    val title = coverTitle.ifEmpty { item.selectFirst(".detail__title a")?.text().orEmpty() }
        .takeIf { it.isNotEmpty() } ?: return null
    return Book(
        id = id,
        title = title,
        author = item.authorNames(".detail__authors"),
        imageUrl = imageUrl,
        registrationCount = item.selectFirst("dt.options__title:containsOwn(登録) + dd")?.int()
    )
}

internal fun Element.bookId(): Int? =
    selectFirst("a[href*=/books/]")?.attr("href")
        ?.let { BOOK_PATH.find(it)?.groupValues?.get(1)?.toIntOrNull() }

internal fun Element.authorNames(listSelector: String): String =
    select("$listSelector a").map { it.text() }.filter { it.isNotEmpty() }.joinToString(",")

internal fun Element.int(): Int? = text().replace(",", "").toIntOrNull()

private val BOOK_PATH = Regex("/books/(\\d+)")
