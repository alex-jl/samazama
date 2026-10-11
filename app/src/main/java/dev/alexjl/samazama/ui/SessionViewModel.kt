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
    var loggingOut by mutableStateOf(false)
        private set
    var logOutFailed by mutableStateOf(false)
        private set

    init {
        refresh()
    }

    fun refresh() {
        loggedIn = null
        logOutFailed = false
        viewModelScope.launch {
            loggedIn = loadResult("SessionViewModel", "Failed to check login") {
                isLoggedInToBookmeter()
            }
        }
    }

    fun onLoggedIn() {
        logOutFailed = false
        loggedIn = Result.success(true)
    }

    fun logOut() {
        if (loggingOut) return
        loggingOut = true
        logOutFailed = false
        viewModelScope.launch {
            loadResult("SessionViewModel", "Failed to log out") { logOutOfBookmeter() }
                .onSuccess { loggedIn = Result.success(false) }
                .onFailure { logOutFailed = true }
            loggingOut = false
        }
    }
}
