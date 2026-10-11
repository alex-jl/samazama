package com.example.samazama.api

import com.example.samazama.data.BookDetails
import com.example.samazama.data.BookEdition
import com.example.samazama.data.BookReview
import com.example.samazama.data.BookReviewPage
import com.example.samazama.data.BookStore
import com.example.samazama.data.Reviewer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmeterBookTest {

    private val html: String = fixture("/book_tokeikan.html")
    private val reviewsJson: String = fixture("/book_tokeikan_reviews.json")
    private val storesJson: String = fixture("/book_tokeikan_stores.json")

    private val stores = listOf(
        BookStore(
            "BOOK☆WALKER",
            "https://bookwalker.jp/de18150c08-5d87-4976-9441-901c65dd202c/?adpcnt=7qM_t"
        ),
        BookStore(
            "Amazon",
            "https://www.amazon.co.jp/dp/4062772949/ref=as_li_tf_tl?camp=247&creative=1211" +
                    "&creativeASIN=4062772949&ie=UTF8&linkCode=as2" +
                    "&tag=bookmeter_book_middle_detail_pc_logoff-22"
        ),
        BookStore(
            "honto",
            "https://ck.jp.ap.valuecommerce.com/servlet/referral?sid=3588324&pid=887261621" +
                    "&vc_url=https%3A%2F%2Fhonto.jp%2Febook%2Fpd_26253538.html%3Fpartnerid%3D02vc01"
        ),
    )

    private val reviews = BookReviewPage(
        reviews = listOf(
            BookReview(
                id = 138218438,
                content = "話の内容はまったく覚えてなかったから、あれがこうでと想像しながら楽しく読めてます（笑）" +
                        "江南君が出版社に就職し、そこのミステリー雑誌の企画で時計館に来ることになったお話でした～",
                createdAt = "2026/10/02",
                reviewer = Reviewer(
                    4710,
                    "火狐＠二児の子育て奮闘中",
                    "https://img.bookmeter.com/profile_image/normals/5/1216194414837864.jpg"
                ),
                niceCount = 12
            ),
            BookReview(
                id = 138216599,
                content = "館シリーズの中でも群を抜いて図書館の予約人数が多くて、読みたいと思ってから読むまでかなり時間が" +
                        "かかってしまった。これは怖いー😱内海くん閉じこもっちゃダメなのよ、中村青司の館には絶対隠し部屋が" +
                        "あるんだから！島田、早くこっち来てー。★3.8",
                createdAt = "2026/10/01",
                reviewer = Reviewer(
                    337679,
                    "くまごろ",
                    "https://bookmeter.com/images/common/profile_image.png"
                ),
                niceCount = 11,
                isSpoiler = true
            ),
        ),
        offset = 0,
        totalCount = 1626
    )

    @Test
    fun `parses the details of the book`() {
        assertEquals(
            BookDetails(
                id = 4965689,
                title = "時計館の殺人<新装改訂版>(上) (講談社文庫 あ 52-23)",
                author = "綾辻 行人",
                imageUrl = "https://m.media-amazon.com/images/I/51c8s8px0KL._SL500_.jpg",
                format = "文庫",
                publisher = "講談社",
                registrationCount = 10106,
                pageCount = 372,
                reviewCount = 1626,
                trialUrl = "https://bookwalker.jp/de18150c08-5d87-4976-9441-901c65dd202c/?sample=1&from=2&adpcnt=7qM_t",
                editions = listOf(
                    BookEdition(4965693, "文庫", "講談社", registrationCount = 9027),
                    BookEdition(573864, "文庫", "講談社", registrationCount = 6995),
                    BookEdition(550290, "新書", "講談社", registrationCount = 695),
                ),
                reviews = reviews,
                stores = stores
            ),
            parseBookDetails(4965689, html, reviews, stores)
        )
    }

    @Test
    fun `parses the stores in priority order with absolute URLs`() {
        assertEquals(stores, parseBookStores(storesJson))
    }

    @Test
    fun `skips stores without a name or URL`() {
        assertEquals(
            listOf(BookStore("Amazon", "https://www.amazon.co.jp/dp/4062772949")),
            parseBookStores(
                "{\"resources\":[" +
                        "{\"official_name\":\"honto\",\"url\":\"\"}," +
                        "{\"official_name\":\"Amazon\",\"url\":\"https://www.amazon.co.jp/dp/4062772949\"}," +
                        "{\"url\":\"https://example.com\"}]}"
            )
        )
        assertEquals(emptyList<BookStore>(), parseBookStores("{\"resources\":[]}"))
    }

    @Test
    fun `parses the reviews and where they sit in the full list`() {
        val page = parseBookReviews(reviewsJson)

        assertEquals(reviews, page)
        assertTrue(page.hasNextPage)
    }

    @Test
    fun `knows when the last reviews have been reached`() {
        val last = parseBookReviews(reviewsJson.replace("\"offset\":0", "\"offset\":1624"))

        assertEquals(1624, last.offset)
        assertFalse(last.hasNextPage)
    }

    @Test
    fun `finds no reviews in an empty response`() {
        assertEquals(
            BookReviewPage(),
            parseBookReviews("{\"metadata\":{\"offset\":0,\"limit\":40,\"count\":0},\"resources\":[]}")
        )
    }

    @Test
    fun `builds the book and review URLs from the id`() {
        assertEquals("https://bookmeter.com/books/4965689", bookUrl(4965689))
        assertEquals(
            "https://bookmeter.com/books/4965689/reviews.json?offset=0&limit=40",
            bookReviewsUrl(4965689)
        )
        assertEquals(
            "https://bookmeter.com/books/4965689/reviews.json?offset=40&limit=10",
            bookReviewsUrl(4965689, offset = 40, limit = 10)
        )
        assertEquals(
            "https://bookmeter.com/api/v1/books/4965689/external_book_stores.json",
            bookStoresUrl(4965689)
        )
    }

    @Test
    fun `finds no details on a page without a book`() {
        assertNull(parseBookDetails(4965689, "<html><body>404</body></html>"))
    }

    @Test
    fun `keeps a book that is missing its optional details`() {
        val bare = html
            .replace(Regex("<div class=\"current-book-detail\">.*?</div>"), "")
            .replace(Regex("<div class=\"group__trial\">.*?</section>"), "")
            .replace(Regex("<dl class=\"bm-details-side.*?</dl>"), "")
        assertNotEquals(html, bare)

        val details = checkNotNull(parseBookDetails(4965689, bare))

        assertEquals("時計館の殺人<新装改訂版>(上) (講談社文庫 あ 52-23)", details.title)
        assertNull(details.format)
        assertNull(details.publisher)
        assertNull(details.trialUrl)
        assertNull(details.registrationCount)
        assertNull(details.pageCount)
        assertEquals(emptyList<BookEdition>(), details.editions)
    }

    private fun fixture(name: String): String =
        checkNotNull(javaClass.getResourceAsStream(name)) {
            "missing test fixture"
        }.reader().use { it.readText() }
}
