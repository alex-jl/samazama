package com.example.samazama.data

data class BookDetails(
    val id: Int,
    val title: String,
    val author: String,
    val imageUrl: String,
    val format: String? = null,
    val publisher: String? = null,
    val registrationCount: Int? = null,
    val pageCount: Int? = null,
    val reviewCount: Int? = null,
    val trialUrl: String? = null,
    val editions: List<BookEdition> = emptyList(),
    val reviews: BookReviewPage = BookReviewPage()
)

data class BookEdition(
    val id: Int,
    val format: String? = null,
    val publisher: String? = null,
    val registrationCount: Int? = null
)

data class BookReviewPage(
    val reviews: List<BookReview> = emptyList(),
    val offset: Int = 0,
    val totalCount: Int = 0
) {
    val hasNextPage: Boolean get() = offset + reviews.size < totalCount
}

data class BookReview(
    val id: Int,
    val content: String,
    val createdAt: String,
    val reviewer: Reviewer,
    val niceCount: Int = 0,
    val commentCount: Int = 0,
    val isSpoiler: Boolean = false
)

data class Reviewer(
    val id: Int,
    val name: String,
    val imageUrl: String
)

val sampleBookDetails = BookDetails(
    id = 4965689,
    title = "時計館の殺人<新装改訂版>(上) (講談社文庫 あ 52-23)",
    author = "綾辻 行人",
    imageUrl = "https://m.media-amazon.com/images/I/51c8s8px0KL._SL500_.jpg",
    format = "文庫",
    publisher = "講談社",
    registrationCount = 10106,
    pageCount = 372,
    reviewCount = 1626,
    trialUrl = "https://bookwalker.jp/de18150c08-5d87-4976-9441-901c65dd202c/?sample=1",
    editions = listOf(
        BookEdition(4965693, "文庫", "講談社", registrationCount = 9027),
        BookEdition(550290, "新書", "講談社", registrationCount = 695),
    ),
    reviews = BookReviewPage(
        reviews = listOf(
            BookReview(
                id = 138218438,
                content = "話の内容はまったく覚えてなかったから、あれがこうでと想像しながら楽しく読めてます（笑）",
                createdAt = "2026/10/02",
                reviewer = Reviewer(
                    4710,
                    "火狐＠二児の子育て奮闘中",
                    "https://img.bookmeter.com/profile_image/normals/5/1216194414837864.jpg"
                ),
                niceCount = 12,
                commentCount = 2
            ),
            BookReview(
                id = 138216599,
                content = "これは怖いー😱内海くん閉じこもっちゃダメなのよ、中村青司の館には絶対隠し部屋があるんだから！",
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
)
