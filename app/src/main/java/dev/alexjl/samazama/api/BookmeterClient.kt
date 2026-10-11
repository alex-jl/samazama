package dev.alexjl.samazama.api

import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

const val BOOKMETER_BASE_URL = "https://bookmeter.com"
const val BOOKMETER_LOGIN_URL = "$BOOKMETER_BASE_URL/login"

/**
 * Non-phone user agent so that we get the full information, rather than stripped-down for mobile.
 */
private const val USER_AGENT = "Samazama/1.0"

/** Only shown to logged-in users; everyone else is redirected to the login page. */
const val BOOKMETER_HOME_URL = "$BOOKMETER_BASE_URL/home"
private const val LOGOUT_URL = "$BOOKMETER_BASE_URL/logout"

class NotLoggedInException(url: String) : IOException("GET $url needs a logged-in session")

suspend fun fetchBookmeterPage(
    url: String,
    requiresLogin: Boolean = false
): String = withContext(Dispatchers.IO) {
    val connection = openBookmeterConnection(url)
    // Pages that need a login answer anonymous requests with a redirect to the login page.
    connection.instanceFollowRedirects = !requiresLogin
    try {
        if (requiresLogin && connection.responseCode in 300..399) {
            throw NotLoggedInException(url)
        }
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("GET $url failed with HTTP ${connection.responseCode}")
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    } finally {
        connection.disconnect()
    }
}

suspend fun isLoggedInToBookmeter(): Boolean = withContext(Dispatchers.IO) {
    val connection = openBookmeterConnection(BOOKMETER_HOME_URL)
    connection.instanceFollowRedirects = false
    try {
        connection.responseCode == HttpURLConnection.HTTP_OK
    } finally {
        connection.disconnect()
    }
}

suspend fun logOutOfBookmeter() {
    val page = try {
        fetchBookmeterPage(BOOKMETER_HOME_URL, requiresLogin = true)
    } catch (e: NotLoggedInException) {
        clearSessionCookies()
        return
    }
    val token = parseCsrfToken(page)
        ?: throw IOException("GET $BOOKMETER_HOME_URL returned a page without a CSRF token")
    postLogout(token)
    clearSessionCookies()
}

fun parseCsrfToken(html: String): String? =
    Jsoup.parse(html).selectFirst("meta[name=csrf-token]")?.attr("content")
        ?.takeIf { it.isNotEmpty() }

private suspend fun postLogout(csrfToken: String) = withContext(Dispatchers.IO) {
    val body = "_method=delete&authenticity_token=${URLEncoder.encode(csrfToken, "UTF-8")}"
        .toByteArray()
    val connection = openBookmeterConnection(LOGOUT_URL).apply {
        requestMethod = "POST"
        instanceFollowRedirects = false
        doOutput = true
        setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        setRequestProperty("Origin", BOOKMETER_BASE_URL)
        setRequestProperty("Referer", BOOKMETER_HOME_URL)
        setFixedLengthStreamingMode(body.size)
    }
    try {
        connection.outputStream.use { it.write(body) }
        if (connection.responseCode !in 200..399) {
            throw IOException("POST $LOGOUT_URL failed with HTTP ${connection.responseCode}")
        }
    } finally {
        connection.disconnect()
    }
}

private fun clearSessionCookies() {
    CookieManager.getInstance().apply {
        removeAllCookies(null)
        flush()
    }
}

/** The login page's web view stores the session cookie, so requests borrow it from there. */
private fun openBookmeterConnection(url: String): HttpURLConnection =
    (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        connectTimeout = 10_000
        readTimeout = 10_000
        setRequestProperty("User-Agent", USER_AGENT)
        setRequestProperty("Accept-Language", "ja")
        CookieManager.getInstance().getCookie(BOOKMETER_BASE_URL)?.let {
            setRequestProperty("Cookie", it)
        }
    }
