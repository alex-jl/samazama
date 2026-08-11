package com.example.samazama

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks
import com.example.samazama.icon.home
import com.example.samazama.icon.menu
import com.example.samazama.icon.search
import com.example.samazama.icon.settings
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
            BookList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookList(
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
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .padding(top = 4.dp),
        ) {
            itemsIndexed(items = books) { i, book -> BookCard(book = book, displayIndex = i + 1) }
        }
    }
}

@Composable
private fun BookCard(book: Book, displayIndex: Int?, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        BookCardContent(book, displayIndex)
    }
}

@Composable
private fun BookCardContent(book: Book, displayIndex: Int?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (displayIndex != null) {
            Column {
                Text(
                    text = displayIndex.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
        Column {
            AsyncImage(
                model = book.imageUrl,
                contentDescription = "Cover image for " + book.title,
                modifier = Modifier
                    .height(100.dp)
                    .padding(vertical = 4.dp, horizontal = 10.dp)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(text = book.author)
        }
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

@OptIn(ExperimentalCoilApi::class)
@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "BookListPreviewDark"
)
@Composable
fun BookListPreview() {
    val context = LocalContext.current
    val previewHandler = AsyncImagePreviewHandler {
        checkNotNull(
            ContextCompat.getDrawable(
                context,
                R.drawable.example_cover
            )
        ).asImage(shareable = true)
    }
    CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
        SamazamaTheme {
            BookList()
        }
    }
}