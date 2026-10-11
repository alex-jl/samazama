package dev.alexjl.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexjl.samazama.api.isLoggedInToBookmeter
import dev.alexjl.samazama.api.logOutOfBookmeter
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    var loggedIn by mutableStateOf<Result<Boolean>?>(null)
        private set

    init {
        refresh()
    }

    fun refresh() {
        loggedIn = null
        viewModelScope.launch {
            loggedIn = loadResult("SessionViewModel", "Failed to check login") {
                isLoggedInToBookmeter()
            }
        }
    }

    fun onLoggedIn() {
        loggedIn = Result.success(true)
    }

    fun logOut() {
        logOutOfBookmeter()
        loggedIn = Result.success(false)
    }
}
