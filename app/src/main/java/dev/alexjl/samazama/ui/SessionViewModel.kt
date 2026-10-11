package dev.alexjl.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexjl.samazama.api.NotLoggedInException
import dev.alexjl.samazama.api.fetchUserProfile
import dev.alexjl.samazama.api.logOutOfBookmeter
import dev.alexjl.samazama.data.UserProfile
import kotlinx.coroutines.launch

class SessionViewModel : ViewModel() {
    var loggedIn by mutableStateOf<Result<Boolean>?>(null)
        private set
    var profile by mutableStateOf<UserProfile?>(null)
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
            val result = loadResult("SessionViewModel", "Failed to load profile") {
                fetchUserProfile()
            }
            val failure = result.exceptionOrNull()
            profile = result.getOrNull()
            loggedIn = when {
                failure == null -> Result.success(true)
                failure is NotLoggedInException -> Result.success(false)
                else -> Result.failure(failure)
            }
        }
    }

    fun logOut() {
        if (loggingOut) return
        loggingOut = true
        logOutFailed = false
        viewModelScope.launch {
            loadResult("SessionViewModel", "Failed to log out") { logOutOfBookmeter() }
                .onSuccess {
                    profile = null
                    loggedIn = Result.success(false)
                }
                .onFailure { logOutFailed = true }
            loggingOut = false
        }
    }
}
