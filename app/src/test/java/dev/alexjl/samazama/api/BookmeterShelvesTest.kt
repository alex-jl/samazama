package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.Book
import dev.alexjl.samazama.data.BookSearchPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmeterShelvesTest {

    private val html: String =
        checkNotNull(javaClass.getResourceAsStream("/shelf_read.html")) {
            "missing test fixture"
        }.reader().use { it.readText() }

    @Test
    fun `parses every book on the shelf, with full titles`() {
        val page = parseShelfPage(html)

        assertEquals(
            BookSearchPage(
                books = listOf(
                    Book(
                        499296,
                        "砂糖の世界史 (岩波ジュニア新書 276)",
                        "川北 稔",
                        "https://m.media-amazon.com/images/I/41p4bOBy2wL._SL500_.jpg",
                        registrationCount = 6341
                    ),
                    Book(
                        22180218,
                        "NEXUS 情報の人類史 上: 人間のネットワーク",
                        "ユヴァル・ノア・ハラリ",
                        "https://m.media-amazon.com/images/I/41R6ux-867L._SL500_.jpg",
                        registrationCount = 2047
                    ),
                ),
                page = 1,
                pageCount = 25,
                totalCount = 497
            ),
            page
        )
        assertTrue(page.hasNextPage)
    }

    @Test
    fun `counts the current page when it is the last one`() {
        val last = parseShelfPage(
            "<ul class=\"bm-pagination\">" +
                    "<li><a class=\"bm-pagination__link\" href=\"/users/123456/books/read?page=24\">24</a></li>" +
                    "<li class=\"active\"><a class=\"bm-pagination__link\" href=\"/users/123456/books/read?page=25\">25</a></li>" +
                    "<li class=\"disable\"><div class=\"bm-pagination__link\">最後</div></li>" +
                    "</ul><div class=\"bm-pagination-notice\">全497件中　481 - 497 件を表示</div>"
        )
        assertEquals(25, last.page)
        assertEquals(25, last.pageCount)
        assertFalse(last.hasNextPage)
    }

    @Test
    fun `finds no books or pages on an empty shelf`() {
        assertEquals(
            BookSearchPage(books = emptyList(), page = 1, pageCount = 0, totalCount = 0),
            parseShelfPage("<div class=\"bm-pagination-notice\">表示項目がありません</div>")
        )
    }

    @Test
    fun `builds the shelf URL from the user, shelf and page`() {
        assertEquals("https://bookmeter.com/users/123456/books/read?page=1", shelfUrl(123456, BookShelf.READ))
        assertEquals("https://bookmeter.com/users/123456/books/wish?page=3", shelfUrl(123456, BookShelf.WISH, page = 3))
    }
}
