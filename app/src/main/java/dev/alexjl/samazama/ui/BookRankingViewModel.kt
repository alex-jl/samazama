package dev.alexjl.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexjl.samazama.api.BookFormat
import dev.alexjl.samazama.api.RankingPeriod
import dev.alexjl.samazama.api.fetchRankings
import dev.alexjl.samazama.data.Book
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class BookRankingViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    var format by mutableStateOf(savedState.get<BookFormat>(FORMAT_KEY) ?: BookFormat.BUNKO)
        private set
    var period by mutableStateOf(savedState.get<RankingPeriod>(PERIOD_KEY) ?: RankingPeriod.MONTH)
        private set
    var rankings by mutableStateOf<Result<List<Book>>?>(null)
        private set

    private var loading: Job? = null

    init {
        load()
    }

    fun selectFormat(format: BookFormat) {
        this.format = format
        savedState[FORMAT_KEY] = format
        load()
    }

    fun selectPeriod(period: RankingPeriod) {
        this.period = period
        savedState[PERIOD_KEY] = period
        load()
    }

    fun retry() = load()

    private fun load() {
        loading?.cancel()
        rankings = null
        loading = viewModelScope.launch {
            rankings = loadResult("BookRankingViewModel", "Failed to load rankings") {
                fetchRankings(format = format, period = period)
            }
        }
    }

    private companion object {
        const val FORMAT_KEY = "format"
        const val PERIOD_KEY = "period"
    }
}
