package com.example.samazama

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samazama.api.fetchRankings
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks
import com.example.samazama.icon.home
import com.example.samazama.icon.menu
import com.example.samazama.icon.search
import com.example.samazama.icon.settings
import com.example.samazama.ui.BookList
import com.example.samazama.ui.BookPreviewTheme
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

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    Surface(modifier) {
        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
            BookRankingScreen()
        }
    }
}

@Composable
private fun BookRankingScreen(modifier: Modifier = Modifier) {
    var retryCount by rememberSaveable { mutableIntStateOf(0) }
    val rankings by produceState<Result<List<Book>>?>(initialValue = null, retryCount) {
        value = null
        value = runCatching { fetchRankings() }
    }

    when (val result = rankings) {
        null -> LoadingScreen(modifier)
        else -> result.fold(
            onSuccess = { books -> BookRankingList(modifier, books) },
            onFailure = { error ->
                Log.e("BookRankingScreen", "Failed to load rankings: ${error::class.qualifiedName}: ${error.message}")
                ErrorScreen(modifier, onRetry = { retryCount++ })
            }
        )
    }
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_rankings),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun ErrorScreen(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.failed_to_load_rankings))
            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .wrapContentSize(),
                onClick = onRetry
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookRankingList(
    modifier: Modifier = Modifier,
    books: List<Book> = sampleBooks
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Column {
                        Text(text = stringResource(R.string.rankings))
                        Text(
                            text = stringResource(R.string.bunko),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { 0 }) {
                        Icon(menu, stringResource(R.string.menu))
                    }
                })
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                NavigationBarItem(
                    selected = false,
                    onClick = { 0 },
                    icon = {
                        Icon(home, stringResource(R.string.home))
                    },
                    label = { Text(stringResource(R.string.home)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { 0 },
                    icon = {
                        Icon(search, stringResource(R.string.search))
                    },
                    label = { Text(stringResource(R.string.search)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { 0 },
                    icon = {
                        Icon(settings, stringResource(R.string.settings))
                    },
                    label = { Text(stringResource(R.string.settings)) }
                )
            }
        }
    ) { innerPadding ->
        BookList(
            books = books,
            modifier = modifier
                .padding(innerPadding)
                .padding(top = 4.dp),
            numbered = true
        )
    }
}

@Composable
fun OnboardingScreen(
    onContinueClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to samazama!!")
        Button(
            modifier = Modifier
                .padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text("Continue")
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

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    SamazamaTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "BookRankingListPreviewDark"
)
@Composable
fun BookRankingListPreview() {
    BookPreviewTheme {
        BookRankingList()
    }
}
