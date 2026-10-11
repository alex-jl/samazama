package com.example.samazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import com.example.samazama.api.BookFormat
import com.example.samazama.api.RankingPeriod
import com.example.samazama.data.ThemeMode
import com.example.samazama.data.loadThemeMode
import com.example.samazama.data.saveThemeMode
import com.example.samazama.ui.AppNavigationBar
import com.example.samazama.ui.AppTopBar
import com.example.samazama.ui.AuthorDetailsScreen
import com.example.samazama.ui.BookDetailsScreen
import com.example.samazama.ui.BookRankingScreen
import com.example.samazama.ui.HomeScreen
import com.example.samazama.ui.NavTab
import com.example.samazama.ui.ProfileScreen
import com.example.samazama.ui.RankingFilters
import com.example.samazama.ui.SearchField
import com.example.samazama.ui.SearchScreen
import com.example.samazama.ui.SettingsScreen
import com.example.samazama.ui.TopBarSection
import com.example.samazama.ui.theme.SamazamaTheme
import java.io.Serializable

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
    HOME(R.string.home, NavTab.HOME),
    RANKINGS(R.string.rankings, null),
    PROFILE(R.string.profile, NavTab.PROFILE),
    SEARCH(R.string.search, NavTab.SEARCH),
    SETTINGS(R.string.settings, NavTab.SETTINGS),
}

private sealed interface Detail : Serializable {
    data class Book(val id: Int) : Detail
    data class Author(val name: String) : Detail
}

@Composable
fun MyApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    var rankingFormat by rememberSaveable { mutableStateOf(BookFormat.BUNKO) }
    var rankingPeriod by rememberSaveable { mutableStateOf(RankingPeriod.MONTH) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchKeyword by rememberSaveable { mutableStateOf<String?>(null) }
    var searchSubmission by rememberSaveable { mutableIntStateOf(0) }
    var openDetails by rememberSaveable { mutableStateOf(emptyList<Detail>()) }
    val detailOpen = openDetails.isNotEmpty()
    val goHome = { screen = Screen.HOME }
    val openBook = { id: Int -> openDetails = openDetails + Detail.Book(id) }
    val openAuthor = { name: String -> openDetails = openDetails + Detail.Author(name) }
    val closeDetail = { openDetails = openDetails.dropLast(1) }

    BackHandler(enabled = screen != Screen.HOME, onBack = goHome)
    BackHandler(enabled = detailOpen, onBack = closeDetail)

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(
                    when (openDetails.lastOrNull()) {
                        is Detail.Book -> R.string.book_details
                        is Detail.Author -> R.string.author_details
                        null -> screen.titleRes
                    }
                ),
                showBackButton = detailOpen || screen != Screen.HOME,
                onBackClick = if (detailOpen) closeDetail else goHome,
                sections = {
                    TopBarSection(visible = screen == Screen.RANKINGS && !detailOpen) {
                        RankingFilters(
                            format = rankingFormat,
                            period = rankingPeriod,
                            onFormatChange = { rankingFormat = it },
                            onPeriodChange = { rankingPeriod = it }
                        )
                    }
                    TopBarSection(visible = screen == Screen.SEARCH && !detailOpen) {
                        SearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    searchKeyword = searchQuery.trim()
                                    searchSubmission++
                                }
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppNavigationBar(
                selected = screen.tab,
                onSelect = { tab ->
                    openDetails = emptyList()
                    screen = when (tab) {
                        NavTab.HOME -> Screen.HOME
                        NavTab.PROFILE -> Screen.PROFILE
                        NavTab.SEARCH -> Screen.SEARCH
                        NavTab.SETTINGS -> Screen.SETTINGS
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = screen,
                modifier = if (detailOpen) Modifier.clearAndSetSemantics {} else Modifier
            ) { target ->
                when (target) {
                    Screen.HOME -> HomeScreen(
                        onProfileClick = { screen = Screen.PROFILE },
                        onRankingsClick = { screen = Screen.RANKINGS },
                        onSearchClick = { screen = Screen.SEARCH }
                    )
                    Screen.RANKINGS -> BookRankingScreen(
                        rankingFormat,
                        rankingPeriod,
                        onBookClick = { openBook(it.id) }
                    )
                    Screen.SEARCH -> SearchScreen(
                        searchKeyword,
                        searchSubmission,
                        onBookClick = { openBook(it.id) }
                    )
                    Screen.PROFILE -> ProfileScreen()
                    Screen.SETTINGS -> SettingsScreen(themeMode, onThemeModeChange)
                }
            }
            AnimatedVisibility(visible = detailOpen, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(targetState = openDetails.lastOrNull()) { detail ->
                        when (detail) {
                            is Detail.Book -> BookDetailsScreen(
                                detail.id,
                                onEditionClick = openBook,
                                onAuthorClick = openAuthor
                            )
                            is Detail.Author -> AuthorDetailsScreen(
                                detail.name,
                                onBookClick = { openBook(it.id) }
                            )
                            null -> Unit
                        }
                    }
                }
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
