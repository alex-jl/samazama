package dev.alexjl.samazama.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexjl.samazama.api.fetchAccountDetails
import dev.alexjl.samazama.data.AccountDetails
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    var details by mutableStateOf<Result<AccountDetails>?>(null)
        private set

    init {
        load()
    }

    fun retry() = load()

    private fun load() {
        details = null
        viewModelScope.launch {
            details = loadResult("AccountViewModel", "Failed to load account") {
                fetchAccountDetails()
            }
        }
    }
}
