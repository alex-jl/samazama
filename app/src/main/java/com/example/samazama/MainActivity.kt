package com.example.samazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.samazama.data.ThemeMode
import com.example.samazama.data.loadThemeMode
import com.example.samazama.data.saveThemeMode
import com.example.samazama.ui.AppNavigationBar
import com.example.samazama.ui.AuthorDetailsScreen
import com.example.samazama.ui.AuthorRoute
import com.example.samazama.ui.BookDetailsScreen
import com.example.samazama.ui.BookRankingScreen
import com.example.samazama.ui.BookRoute
import com.example.samazama.ui.HomeRoute
import com.example.samazama.ui.HomeScreen
import com.example.samazama.ui.NavTab
import com.example.samazama.ui.ProfileRoute
import com.example.samazama.ui.ProfileScreen
import com.example.samazama.ui.RankingsRoute
import com.example.samazama.ui.SearchRoute
import com.example.samazama.ui.SearchScreen
import com.example.samazama.ui.SettingsRoute
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

private const val TRANSITION_MILLIS = 200

@Composable
fun MyApp(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentEntry by navController.currentBackStackEntryAsState()
    val selectedTab = remember(currentEntry) {
        if (currentEntry == null) NavTab.HOME else navController.selectedTab()
    }
    // The rankings and search keep their state for as long as the app is open,
    // rather than only while they are on the back stack.
    val appScope = LocalViewModelStoreOwner.current

    val goBack: () -> Unit = {
        if (navController.previousBackStackEntry != null) navController.popBackStack()
    }
    val goHome = { navController.popBackStack<HomeRoute>(inclusive = false) }
    val openTab = { route: Any ->
        navController.navigate(route) {
            popUpTo<HomeRoute>()
            launchSingleTop = true
        }
    }
    val openBook = { id: Int -> navController.navigate(BookRoute(id)) }
    val openAuthor = { name: String -> navController.navigate(AuthorRoute(name)) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AppNavigationBar(
                selected = selectedTab,
                onSelect = { tab ->
                    when (tab) {
                        NavTab.HOME -> goHome()
                        NavTab.PROFILE -> openTab(ProfileRoute)
                        NavTab.SEARCH -> openTab(SearchRoute)
                        NavTab.SETTINGS -> openTab(SettingsRoute)
                    }
                }
            )
        },
        // Each screen's top bar handles the status bar itself.
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(TRANSITION_MILLIS)) },
            exitTransition = { fadeOut(tween(TRANSITION_MILLIS)) }
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onProfileClick = { openTab(ProfileRoute) },
                    onRankingsClick = { navController.navigate(RankingsRoute) },
                    onSearchClick = { openTab(SearchRoute) }
                )
            }
            composable<RankingsRoute> {
                BookRankingScreen(
                    viewModel = viewModel(viewModelStoreOwner = checkNotNull(appScope)),
                    onBackClick = goBack,
                    onBookClick = { openBook(it.id) }
                )
            }
            composable<ProfileRoute> {
                ProfileScreen(onBackClick = goBack)
            }
            composable<SearchRoute> {
                SearchScreen(
                    viewModel = viewModel(viewModelStoreOwner = checkNotNull(appScope)),
                    onBackClick = goBack,
                    onBookClick = { openBook(it.id) }
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(themeMode, onThemeModeChange, onBackClick = goBack)
            }
            composable<BookRoute> {
                BookDetailsScreen(
                    viewModel = viewModel(),
                    onBackClick = goBack,
                    onEditionClick = openBook,
                    onAuthorClick = openAuthor
                )
            }
            composable<AuthorRoute> {
                AuthorDetailsScreen(
                    viewModel = viewModel(),
                    onBackClick = goBack,
                    onBookClick = { openBook(it.id) }
                )
            }
        }
    }
}

private fun NavController.selectedTab(): NavTab? = when {
    isOnBackStack<ProfileRoute>() -> NavTab.PROFILE
    isOnBackStack<SearchRoute>() -> NavTab.SEARCH
    isOnBackStack<SettingsRoute>() -> NavTab.SETTINGS
    isOnBackStack<RankingsRoute>() -> null
    else -> NavTab.HOME
}

private inline fun <reified T : Any> NavController.isOnBackStack(): Boolean =
    try {
        getBackStackEntry<T>()
        true
    } catch (e: IllegalArgumentException) {
        false
    }

@Preview(widthDp = 320)
@Composable
fun MyAppPreview() {
    SamazamaTheme {
        MyApp(ThemeMode.SYSTEM, onThemeModeChange = {}, modifier = Modifier.fillMaxSize())
    }
}
