package com.example.samazama.data

data class AuthorDetails(
    val name: String,
    val profile: String? = null,
    val awards: List<AuthorAward> = emptyList(),
    val books: BookSearchPage = BookSearchPage()
)

data class AuthorAward(
    val award: String,
    val year: Int? = null,
    val bookTitle: String? = null,
    val bookAsin: String? = null
)

val sampleAuthorDetails = AuthorDetails(
    name = "辻村 深月",
    profile = "山梨県生まれ。千葉大学教育学部卒。平成16年/2004年、メフィスト賞を受賞し、作家デビュー。",
    awards = listOf(
        AuthorAward("第31回メフィスト賞", 2004, "冷たい校舎の時は止まる", "4062758229"),
        AuthorAward("第147回直木賞", 2012, "鍵のない夢を見る", "4167903989"),
    ),
    books = BookSearchPage(
        books = sampleBooks.distinctBy { it.id },
        page = 1,
        pageCount = 13,
        totalCount = 249
    )
)
