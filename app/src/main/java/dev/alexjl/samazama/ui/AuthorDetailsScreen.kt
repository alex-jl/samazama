package dev.alexjl.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexjl.samazama.R
import dev.alexjl.samazama.data.AuthorAward
import dev.alexjl.samazama.data.AuthorDetails
import dev.alexjl.samazama.data.Book
import dev.alexjl.samazama.data.sampleAuthorDetails
import java.text.NumberFormat

private val EdgePadding = 16.dp
private val AwardYearWidth = 56.dp

@Composable
fun AuthorDetailsScreen(
    viewModel: AuthorDetailsViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(
        title = stringResource(R.string.author_details),
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        when (val result = viewModel.details) {
            null -> LoadingScreen(stringResource(R.string.loading_author))
            else -> result.fold(
                onSuccess = {
                    AuthorDetailsContent(
                        details = it,
                        books = viewModel.books,
                        hasMoreBooks = viewModel.hasMoreBooks,
                        loadingMoreBooks = viewModel.loadingMoreBooks,
                        onLoadMoreBooks = viewModel::loadMoreBooks,
                        onBookClick = onBookClick
                    )
                },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_load_author),
                        onRetry = viewModel::retry
                    )
                }
            )
        }
    }
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
