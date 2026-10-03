package com.example.samazama.api

import com.example.samazama.data.Book
import com.example.samazama.data.BookSearchPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmeterSearchTest {

    private val html: String =
        checkNotNull(javaClass.getResourceAsStream("/search_fire_dome.html")) {
            "missing test fixture"
        }.reader().use { it.readText() }

    @Test
    fun `parses every book in the results, with full titles`() {
        val page = parseSearchPage(html)

        assertEquals(
            BookSearchPage(
                books = listOf(
                    Book(
                        23218551,
                        "ファイア・ドーム (上)",
                        "辻村 深月",
                        "https://m.media-amazon.com/images/I/51mWS+-BJUL._SL500_.jpg",
                        registrationCount = 7648
                    ),
                    Book(
                        21731737,
                        "欲しがりな義妹に堪忍袋の緒が切れました～婚約者を奪ったうえに、我が家を乗っ取るなんて許しません～ (Mノベルスf)",
                        "重原水鳥",
                        "https://m.media-amazon.com/images/I/51IG0jPzzSL._SL500_.jpg",
                        registrationCount = 30
                    ),
                ),
                page = 1,
                pageCount = 1,
                totalCount = 2
            ),
            page
        )
        assertFalse(page.hasNextPage)
    }

    @Test
    fun `works out the page number and page count from the pagination data`() {
        val middle = parseSearchPage(paginationHtml(offset = 20, perPage = 20, total = 51))
        assertEquals(2, middle.page)
        assertEquals(3, middle.pageCount)
        assertEquals(51, middle.totalCount)
        assertTrue(middle.hasNextPage)

        val last = parseSearchPage(paginationHtml(offset = 40, perPage = 20, total = 51))
        assertEquals(3, last.page)
        assertEquals(3, last.pageCount)
        assertFalse(last.hasNextPage)

        val exact = parseSearchPage(paginationHtml(offset = 0, perPage = 20, total = 40))
        assertEquals(2, exact.pageCount)
    }

    @Test
    fun `builds the search URL from the keyword and page`() {
        assertEquals(
            "https://bookmeter.com/search?keyword=%E3%83%95%E3%82%A1%E3%82%A4%E3%82%A2%E3%83%BB%E3%83%89%E3%83%BC%E3%83%A0&partial=true&sort=recommended&page=1",
            searchUrl("ファイア・ドーム")
        )
        assertEquals(
            "https://bookmeter.com/search?keyword=abc&partial=true&sort=recommended&page=3",
            searchUrl("abc", page = 3)
        )
    }

    @Test
    fun `finds no books or pages in results without a match`() {
        assertEquals(
            BookSearchPage(books = emptyList(), page = 1, pageCount = 0, totalCount = 0),
            parseSearchPage("<span id=\"contents\"></span>")
        )
    }

    private fun paginationHtml(offset: Int, perPage: Int, total: Int): String =
        "<span data-pagination=\"{&quot;pagination&quot;:{&quot;sort_field&quot;:&quot;recommended&quot;," +
                "&quot;sort_order&quot;:&quot;desc&quot;,&quot;offset&quot;:$offset," +
                "&quot;per_page&quot;:$perPage,&quot;total&quot;:$total}}\" id=\"contents\"></span>"
}
