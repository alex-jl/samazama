package com.example.samazama.api

import com.example.samazama.data.Book
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BookmeterRankingsTest {

    private val html: String =
        checkNotNull(javaClass.getResourceAsStream("/bunko_monthly_read_book.html")) {
            "missing test fixture"
        }.reader().use { it.readText() }

    @Test
    fun `parses every book on the page, in ranking order`() {
        val books = parseRankingBooks(html)

        assertEquals(
            listOf(
                Book(
                    23467261,
                    "幽冥の岸　十二国記 (新潮文庫 お 37-66)",
                    "小野不由美",
                    "https://m.media-amazon.com/images/I/51jntQFaCmL._SL500_.jpg"
                ),
                Book(
                    22416936,
                    "一次元の挿し木 (宝島社文庫 『このミス』大賞シリーズ)",
                    "松下 龍之介",
                    "https://m.media-amazon.com/images/I/51mxzx-4pKL._SL500_.jpg"
                ),
                Book(
                    21832291,
                    "白鳥とコウモリ（上） (幻冬舎文庫)",
                    "東野 圭吾",
                    "https://m.media-amazon.com/images/I/31sfrOEvWhL._SL500_.jpg"
                ),
                Book(
                    580841,
                    "十角館の殺人 <新装改訂版> (講談社文庫 あ 52-14)",
                    "綾辻 行人",
                    "https://m.media-amazon.com/images/I/41VtHAw3hyL._SL500_.jpg"
                ),
                Book(
                    23269675,
                    "本屋さんのある街で (文春文庫 ふ 53-2)",
                    "凪良 ゆう,瀬尾 まいこ,坂木 司,一穂 ミチ,三浦 しをん",
                    "https://m.media-amazon.com/images/I/516Y8vVJhkL._SL500_.jpg"
                ),
            ),
            books
        )
    }

    @Test
    fun `builds the ranking URL from the kind, format and period`() {
        assertEquals(
            "https://bookmeter.com/rankings/latest/read_book/bunko/month",
            rankingUrl(RankingKind.READ_BOOK, BookFormat.BUNKO, RankingPeriod.MONTH)
        )
        assertEquals(
            "https://bookmeter.com/rankings/latest/wish_book/bunko/month",
            rankingUrl(RankingKind.WISH_BOOK, BookFormat.BUNKO, RankingPeriod.MONTH)
        )
        assertEquals(
            "https://bookmeter.com/rankings/latest/read_book/light_novel/week",
            rankingUrl(RankingKind.READ_BOOK, BookFormat.LIGHT_NOVEL, RankingPeriod.WEEK)
        )
        assertEquals(
            "https://bookmeter.com/rankings/latest/read_book/comic/day",
            rankingUrl(RankingKind.READ_BOOK, BookFormat.COMIC, RankingPeriod.DAY)
        )
        assertEquals(
            "https://bookmeter.com/rankings/latest/read_book/tankoubon/month",
            rankingUrl(RankingKind.READ_BOOK, BookFormat.TANKOUBON, RankingPeriod.MONTH)
        )
        assertEquals(
            "https://bookmeter.com/rankings/latest/read_book/others/month",
            rankingUrl(RankingKind.READ_BOOK, BookFormat.OTHERS, RankingPeriod.MONTH)
        )
    }

    @Test
    fun `finds no books on a page without a ranking`() {
        assertEquals(emptyList<Book>(), parseRankingBooks("<html><body>404</body></html>"))
    }

    @Test
    fun `skips a card that is missing its cover`() {
        val coverless = html.replace(Regex("<img[^>]*51mxzx[^>]*>"), "")
        assertNotEquals(html, coverless)

        val titles = parseRankingBooks(coverless).map { it.title }

        assertEquals(
            listOf(
                "幽冥の岸　十二国記 (新潮文庫 お 37-66)",
                "白鳥とコウモリ（上） (幻冬舎文庫)",
                "十角館の殺人 <新装改訂版> (講談社文庫 あ 52-14)",
                "本屋さんのある街で (文春文庫 ふ 53-2)"
            ),
            titles
        )
    }
}
