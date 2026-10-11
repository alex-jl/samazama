package dev.alexjl.samazama.ui

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.BOOKMETER_BASE_URL
import dev.alexjl.samazama.api.BOOKMETER_LOGIN_URL
import dev.alexjl.samazama.api.isLoggedInToBookmeter
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier
) = AppScreen(
    title = stringResource(R.string.log_in),
    onBackClick = onBackClick,
    modifier = modifier
) {
    val scope = rememberCoroutineScope()
    val currentOnLoggedIn by rememberUpdatedState(onLoggedIn)

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                // Bookmeter's login form does not work without JavaScript.
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    private var finished = false

                    override fun onPageFinished(view: WebView, url: String) {
                        val leftLoginPage =
                            url.startsWith(BOOKMETER_BASE_URL) && !url.startsWith(BOOKMETER_LOGIN_URL)
                        if (finished || !leftLoginPage) return
                        CookieManager.getInstance().flush()
                        scope.launch {
                            val loggedIn = loadResult("LoginScreen", "Failed to check login") {
                                isLoggedInToBookmeter()
                            }.getOrDefault(false)
                            if (loggedIn && !finished) {
                                finished = true
                                currentOnLoggedIn()
                            }
                        }
                    }
                }
                loadUrl(BOOKMETER_LOGIN_URL)
            }
        },
        modifier = Modifier.fillMaxSize(),
        onRelease = { it.destroy() }
    )
}
