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

private val ENTITY = Regex("&(#[xX][0-9a-fA-F]+|#\\d+|[a-zA-Z]+);")
private val NAMED_ENTITIES = mapOf(
    "amp" to "&",
    "apos" to "'",
    "gt" to ">",
    "lt" to "<",
    "nbsp" to " ",
    "quot" to "\"",
)

fun unescapeHtml(text: String): String {
    if ('&' !in text) return text
    return ENTITY.replace(text) { match ->
        val body = match.groupValues[1]
        when {
            body.startsWith("#x") || body.startsWith("#X") -> codePointToString(body.drop(2), radix = 16)
            body.startsWith("#") -> codePointToString(body.drop(1), radix = 10)
            else -> NAMED_ENTITIES[body.lowercase()]
        } ?: match.value
    }
}

private fun codePointToString(digits: String, radix: Int): String? {
    val codePoint = digits.toIntOrNull(radix) ?: return null
    return runCatching { String(Character.toChars(codePoint)) }.getOrNull()
}
