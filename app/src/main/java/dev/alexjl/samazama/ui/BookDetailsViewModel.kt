package dev.alexjl.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.alexjl.samazama.api.fetchBookDetails
import dev.alexjl.samazama.api.fetchBookReviews
import dev.alexjl.samazama.data.BookDetails
import dev.alexjl.samazama.data.BookReview
import dev.alexjl.samazama.data.BookReviewPage
import kotlinx.coroutines.launch

class BookDetailsViewModel(savedState: SavedStateHandle) : ViewModel() {
    private val bookId = savedState.toRoute<BookRoute>().id

    var details by mutableStateOf<Result<BookDetails>?>(null)
        private set
    var reviews by mutableStateOf(emptyList<BookReview>())
        private set
    var hasMoreReviews by mutableStateOf(false)
        private set
    var loadingMoreReviews by mutableStateOf(false)
        private set

    private var lastReviewPage = BookReviewPage()

    init {
        load()
    }

    fun retry() = load()

    fun loadMoreReviews() {
        if (loadingMoreReviews) return
        loadingMoreReviews = true
        viewModelScope.launch {
            loadResult(TAG, "Failed to load reviews") {
                fetchBookReviews(
                    bookId,
                    offset = lastReviewPage.offset + lastReviewPage.reviews.size
                )
            }.onSuccess(::addReviews)
            loadingMoreReviews = false
        }
    }

    private fun load() {
        details = null
        viewModelScope.launch {
            val result = loadResult(TAG, "Failed to load book") { fetchBookDetails(bookId) }
            result.onSuccess {
                reviews = emptyList()
                addReviews(it.reviews)
            }
            details = result
        }
    }

    private fun addReviews(page: BookReviewPage) {
        val known = reviews.mapTo(HashSet()) { it.id }
        reviews = reviews + page.reviews.filter { known.add(it.id) }
        lastReviewPage = page
        hasMoreReviews = page.hasNextPage
    }

    private companion object {
        const val TAG = "BookDetailsViewModel"
    }
}
