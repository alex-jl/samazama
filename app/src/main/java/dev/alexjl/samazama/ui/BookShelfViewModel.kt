package dev.alexjl.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.alexjl.samazama.api.fetchShelf
import dev.alexjl.samazama.data.BookPage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookShelfViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    private val route = savedState.toRoute<BookShelfRoute>()

    val shelf = route.shelf
    var books by mutableStateOf<Result<BookPage>?>(null)
        private set

    private var page = savedState.get<Int>(PAGE_KEY) ?: 1
    private var loading: Job? = null

    init {
        load()
    }

    fun goToPage(page: Int) {
        this.page = page
        savedState[PAGE_KEY] = page
        load()
    }

    fun retry() = load()

    private fun load() {
        loading?.cancel()
        books = null
        loading = viewModelScope.launch {
            books = loadResult("BookShelfViewModel", "Failed to load shelf") {
                fetchShelf(route.userId, shelf, page)
            }
        }
    }

    private companion object {
        const val PAGE_KEY = "page"
    }
}
