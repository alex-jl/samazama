package com.example.samazama.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

const val BOOKMETER_BASE_URL = "https://bookmeter.com"

/**
 * Non-phone user agent so that we get the full information, rather than stripped-down for mobile.
 */
private const val USER_AGENT = "Samazama/1.0"

suspend fun fetchBookmeterPage(url: String): String = withContext(Dispatchers.IO) {
    val connection = (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 10_000
        readTimeout = 10_000
        setRequestProperty("User-Agent", USER_AGENT)
        setRequestProperty("Accept-Language", "ja")
    }
    try {
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("GET $url failed with HTTP ${connection.responseCode}")
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    } finally {
        connection.disconnect()
    }
}
