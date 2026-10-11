package com.example.samazama.ui

import android.util.Log
import kotlinx.coroutines.CancellationException

internal suspend fun <T> loadResult(tag: String, failure: String, block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Log.e(tag, "$failure: ${e::class.qualifiedName}: ${e.message}")
        Result.failure(e)
    }
