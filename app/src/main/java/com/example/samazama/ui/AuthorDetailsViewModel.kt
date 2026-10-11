package com.example.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.samazama.api.fetchAuthorBooks
import com.example.samazama.api.fetchAuthorDetails
import com.example.samazama.data.AuthorDetails
import com.example.samazama.data.Book
import com.example.samazama.data.BookSearchPage
import kotlinx.coroutines.launch

class AuthorDetailsViewModel(savedState: SavedStateHandle) : ViewModel() {
    private val authorName = savedState.toRoute<AuthorRoute>().name

    var details by mutableStateOf<Result<AuthorDetails>?>(null)
        private set
    var books by mutableStateOf(emptyList<Book>())
        private set
    var hasMoreBooks by mutableStateOf(false)
        private set
    var loadingMoreBooks by mutableStateOf(false)
        private set

    private var lastBookPage = BookSearchPage()

    init {
        load()
    }

    fun retry() = load()

    fun loadMoreBooks() {
        if (loadingMoreBooks) return
        loadingMoreBooks = true
        viewModelScope.launch {
            loadResult(TAG, "Failed to load books") {
                fetchAuthorBooks(authorName, page = lastBookPage.page + 1)
            }.onSuccess(::addBooks)
            loadingMoreBooks = false
        }
    }

    private fun load() {
        details = null
        viewModelScope.launch {
            val result = loadResult(TAG, "Failed to load author") { fetchAuthorDetails(authorName) }
            result.onSuccess {
                books = emptyList()
                addBooks(it.books)
            }
            details = result
        }
    }

    private fun addBooks(page: BookSearchPage) {
        val known = books.mapTo(HashSet()) { it.id }
        books = books + page.books.filter { known.add(it.id) }
        lastBookPage = page
        hasMoreBooks = page.hasNextPage
    }

    private companion object {
        const val TAG = "AuthorDetailsViewModel"
    }
}
