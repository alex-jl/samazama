package com.example.samazama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.samazama.ui.AppNavigationBar
import com.example.samazama.ui.AppTopBar
import com.example.samazama.ui.BookRankingScreen
import com.example.samazama.ui.HomeScreen
import com.example.samazama.ui.theme.SamazamaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SamazamaTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

private enum class Screen(@StringRes val titleRes: Int) {
    HOME(R.string.home_title),
    RANKINGS(R.string.rankings),
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var screen by rememberSaveable { mutableStateOf(Screen.HOME) }
    val goHome = { screen = Screen.HOME }

    BackHandler(enabled = screen != Screen.HOME, onBack = goHome)

    Scaffold(
        modifier = modifier,
        topBar = {
            AppTopBar(
                title = stringResource(screen.titleRes),
                showBackButton = screen != Screen.HOME,
                onBackClick = goHome
            )
        },
        bottomBar = {
            AppNavigationBar(onHomeClick = goHome, homeSelected = screen == Screen.HOME)
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = screen,
            modifier = Modifier.padding(innerPadding)
        ) { target ->
            when (target) {
                Screen.HOME -> HomeScreen(onRankingsClick = { screen = Screen.RANKINGS })
                Screen.RANKINGS -> BookRankingScreen()
            }
        }
    }
}

@Preview(widthDp = 320)
@Composable
fun MyAppPreview() {
    SamazamaTheme {
        MyApp(Modifier.fillMaxSize())
    }
}
