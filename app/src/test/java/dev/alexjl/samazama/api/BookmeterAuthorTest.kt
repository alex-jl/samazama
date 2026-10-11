package dev.alexjl.samazama.api

import dev.alexjl.samazama.data.AuthorAward
import dev.alexjl.samazama.data.AuthorDetails
import dev.alexjl.samazama.data.Book
import dev.alexjl.samazama.data.BookSearchPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BookmeterAuthorTest {

    private val html: String =
        checkNotNull(javaClass.getResourceAsStream("/author_tsujimura.html")) {
            "missing test fixture"
        }.reader().use { it.readText() }

    private val books = BookSearchPage(
        books = listOf(
            Book(
                11453932,
                "かがみの孤城",
                "辻村 深月",
                "https://m.media-amazon.com/images/I/51Knjs5LhbL._SL500_.jpg",
                registrationCount = 59633
            ),
        ),
        page = 1,
        pageCount = 13,
        totalCount = 249
    )

    @Test
    fun `parses the name, profile and awards of the author`() {
        assertEquals(
            AuthorDetails(
                name = "辻村 深月",
                profile = "山梨県生まれ。千葉大学教育学部卒。平成16年/2004年、メフィスト賞を受賞し、作家デビュー。",
                awards = listOf(
                    AuthorAward("第31回メフィスト賞", 2004, "冷たい校舎の時は止まる", "4062758229"),
                    AuthorAward("第32回吉川英治文学新人賞", 2010, "ツナグ", "4101388814"),
                    AuthorAward("第147回直木賞", 2012, "鍵のない夢を見る", "4167903989"),
                    AuthorAward("第4回静岡書店大賞［小説部門］", 2015, "朝が来る", "4167991330"),
                    AuthorAward("第15回2018年本屋大賞", 2018, "かがみの孤城", "4591153320"),
                ),
                books = books
            ),
            parseAuthorDetails(html, books)
        )
    }

    @Test
    fun `keeps an author who has no profile or awards`() {
        val bare = html.replace(Regex("<section class=\"sidebar__group\">.*?</section>"), "")

        assertEquals(AuthorDetails(name = "辻村 深月"), parseAuthorDetails(bare))
    }

    @Test
    fun `keeps an award that is not tied to a book`() {
        val honour = html.replace(
            Regex("<ul class=\"list__awards\">.*?</ul>"),
            "<ul class=\"list__awards\"><li>紫綬褒章</li></ul>"
        )

        assertEquals(listOf(AuthorAward("紫綬褒章")), parseAuthorDetails(honour)?.awards)
    }

    @Test
    fun `finds no details on a page without an author`() {
        assertNull(parseAuthorDetails("<html><body>404</body></html>"))
    }

    @Test
    fun `builds the author URLs from the name`() {
        assertEquals(
            "https://bookmeter.com/search?author=%E8%BE%BB%E6%9D%91+%E6%B7%B1%E6%9C%88",
            authorUrl("辻村 深月")
        )
        assertEquals(
            "https://bookmeter.com/search?author=%E8%BE%BB%E6%9D%91+%E6%B7%B1%E6%9C%88" +
                    "&partial=true&sort=recommended&page=2",
            authorBooksUrl("辻村 深月", page = 2)
        )
    }

    @Test
    fun `reads the author name back out of a link`() {
        assertEquals(
            "辻村 深月",
            authorNameFromUrl("https://bookmeter.com/search?author=%E8%BE%BB%E6%9D%91+%E6%B7%B1%E6%9C%88")
        )
        assertEquals(
            "辻村 深月",
            authorNameFromUrl("/search?keyword=x&author=%E8%BE%BB%E6%9D%91+%E6%B7%B1%E6%9C%88&sort=recommended")
        )
        assertNull(authorNameFromUrl("https://bookmeter.com/search?keyword=x"))
    }
}
