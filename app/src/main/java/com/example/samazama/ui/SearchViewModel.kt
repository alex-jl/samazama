package com.example.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.samazama.api.searchBooks
import com.example.samazama.data.BookSearchPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    var query by mutableStateOf(savedState.get<String>(QUERY_KEY).orEmpty())
        private set
    var keyword by mutableStateOf(savedState.get<String>(KEYWORD_KEY))
        private set
    var results by mutableStateOf<Result<BookSearchPage>?>(null)
        private set

    private var page = savedState.get<Int>(PAGE_KEY) ?: 1
    private var loading: Job? = null

    init {
        load()
    }

    fun updateQuery(query: String) {
        this.query = query
        savedState[QUERY_KEY] = query
    }

    fun search() {
        if (query.isBlank()) return
        keyword = query.trim()
        savedState[KEYWORD_KEY] = keyword
        goToPage(1)
    }

    fun goToPage(page: Int) {
        this.page = page
        savedState[PAGE_KEY] = page
        load()
    }

    fun retry() = load()

    private fun load() {
        val keyword = keyword ?: return
        loading?.cancel()
        results = null
        loading = viewModelScope.launch {
            results = loadResult("SearchViewModel", "Failed to search") {
                searchBooks(keyword, page)
            }
        }
    }

    private companion object {
        const val QUERY_KEY = "query"
        const val KEYWORD_KEY = "keyword"
        const val PAGE_KEY = "page"
    }
}
