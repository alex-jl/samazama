package com.example.samazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.samazama.data.ThemeMode
import com.example.samazama.data.loadThemeMode
import com.example.samazama.data.saveThemeMode
import com.example.samazama.ui.AppNavigationBar
import com.example.samazama.ui.AppTopBar
import com.example.samazama.ui.BookRankingScreen
import com.example.samazama.ui.HomeScreen
import com.example.samazama.ui.NavTab
import com.example.samazama.ui.SearchScreen
import com.example.samazama.ui.SettingsScreen
import com.example.samazama.ui.theme.SamazamaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var themeMode by remember { mutableStateOf(loadThemeMode(context)) }
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            SamazamaTheme(darkTheme = darkTheme) {
                MyApp(
                    themeMode = themeMode,
                    onThemeModeChange = {
                        themeMode = it
                        saveThemeMode(context, it)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private enum class Screen(@StringRes val titleRes: Int, val tab: NavTab?) {
    HOME(R.string.home_title, NavTab.HOME),
    RANKINGS(R.string.rankings, null),
    SEARCH(R.string.search, NavTab.SEARCH),
    SETTINGS(R.string.settings, NavTab.SETTINGS),
}

@Composable
fun MyApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    val goHome = { screen = Screen.HOME }

    BackHandler(enabled = screen != Screen.HOME, onBack = goHome)

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(screen.titleRes),
                showBackButton = screen.tab == null,
                onBackClick = goHome
            )
        },
        bottomBar = {
            AppNavigationBar(
                selected = screen.tab,
                onSelect = { tab ->
                    screen = when (tab) {
                        NavTab.HOME -> Screen.HOME
                        NavTab.SEARCH -> Screen.SEARCH
                        NavTab.SETTINGS -> Screen.SETTINGS
                    }
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = screen,
            modifier = Modifier.padding(innerPadding)
        ) { target ->
            when (target) {
                Screen.HOME -> HomeScreen(onRankingsClick = { screen = Screen.RANKINGS })
                Screen.RANKINGS -> BookRankingScreen()
                Screen.SEARCH -> SearchScreen()
                Screen.SETTINGS -> SettingsScreen(themeMode, onThemeModeChange)
            }
        }
    }
}

@Preview(widthDp = 320)
@Composable
fun MyAppPreview() {
    SamazamaTheme {
        MyApp(ThemeMode.SYSTEM, onThemeModeChange = {}, modifier = Modifier.fillMaxSize())
    }
}
