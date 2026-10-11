package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samazama.R
import com.example.samazama.api.fetchAuthorBooks
import com.example.samazama.api.fetchAuthorDetails
import com.example.samazama.data.AuthorAward
import com.example.samazama.data.AuthorDetails
import com.example.samazama.data.Book
import com.example.samazama.data.sampleAuthorDetails
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.NumberFormat

private val EdgePadding = 16.dp
private val AwardYearWidth = 56.dp

@Composable
fun AuthorDetailsScreen(
    authorName: String,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    var retryCount by rememberSaveable { mutableIntStateOf(0) }
    val details by produceState<Result<AuthorDetails>?>(initialValue = null, authorName, retryCount) {
        value = null
        value = try {
            Result.success(fetchAuthorDetails(authorName))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("AuthorDetailsScreen", "Failed to load author: ${e::class.qualifiedName}: ${e.message}")
            Result.failure(e)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val result = details) {
            null -> LoadingScreen(stringResource(R.string.loading_author))
            else -> result.fold(
                onSuccess = { LoadedAuthorDetails(authorName, it, onBookClick) },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_load_author),
                        onRetry = { retryCount++ }
                    )
                }
            )
        }
    }
}

@Composable
private fun LoadedAuthorDetails(
    authorName: String,
    details: AuthorDetails,
    onBookClick: (Book) -> Unit
) {
    val scope = rememberCoroutineScope()
    var books by remember(details) { mutableStateOf(details.books.books.distinctBy { it.id }) }
    var lastPage by remember(details) { mutableStateOf(details.books) }
    var loadingMore by remember(details) { mutableStateOf(false) }

    AuthorDetailsContent(
        details = details,
        books = books,
        hasMoreBooks = lastPage.hasNextPage,
        loadingMoreBooks = loadingMore,
        onLoadMoreBooks = {
            loadingMore = true
            scope.launch {
                try {
                    val page = fetchAuthorBooks(authorName, page = lastPage.page + 1)
                    val known = books.mapTo(HashSet()) { it.id }
                    books = books + page.books.filter { known.add(it.id) }
                    lastPage = page
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(
                        "AuthorDetailsScreen",
                        "Failed to load books: ${e::class.qualifiedName}: ${e.message}"
                    )
                } finally {
                    loadingMore = false
                }
            }
        },
        onBookClick = onBookClick
    )
}

@Composable
private fun AuthorDetailsContent(
    details: AuthorDetails,
    books: List<Book>,
    hasMoreBooks: Boolean,
    loadingMoreBooks: Boolean,
    onLoadMoreBooks: () -> Unit,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getIntegerInstance() }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Text(
                text = details.name,
                modifier = Modifier.padding(
                    start = EdgePadding,
                    end = EdgePadding,
                    top = EdgePadding
                ),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        if (details.profile != null) {
            item { SectionHeader(stringResource(R.string.profile)) }
            item {
                Text(
                    text = details.profile,
                    modifier = Modifier.padding(horizontal = EdgePadding),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (details.awards.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.awards)) }
            items(details.awards) { award -> AwardEntry(award) }
        }
        item {
            SectionHeader(
                stringResource(
                    R.string.books_with_count,
                    numberFormat.format(details.books.totalCount)
                )
            )
        }
        if (books.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_results),
                    modifier = Modifier.padding(horizontal = EdgePadding, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(books, key = { it.id }) { book ->
            BookListEntry(book = book, onClick = { onBookClick(book) })
        }
        if (hasMoreBooks) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (loadingMoreBooks) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    } else {
                        TextButton(onClick = onLoadMoreBooks) {
                            Text(stringResource(R.string.show_more))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AwardEntry(award: AuthorAward, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(horizontal = EdgePadding, vertical = 6.dp)) {
        Text(
            text = award.year?.toString().orEmpty(),
            modifier = Modifier.width(AwardYearWidth),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum")
        )
        Column {
            Text(text = award.award, style = MaterialTheme.typography.bodyMedium)
            if (award.bookTitle != null) {
                Text(
                    text = award.bookTitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 720)
@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 720,
    uiMode = UI_MODE_NIGHT_YES,
    name = "AuthorDetailsContentPreviewDark"
)
@Composable
private fun AuthorDetailsContentPreview() {
    BookPreviewTheme {
        AuthorDetailsContent(
            details = sampleAuthorDetails,
            books = sampleAuthorDetails.books.books,
            hasMoreBooks = true,
            loadingMoreBooks = false,
            onLoadMoreBooks = {},
            onBookClick = {}
        )
    }
}
