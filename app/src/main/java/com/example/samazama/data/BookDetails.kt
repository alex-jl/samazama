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
