package dev.alexjl.samazama.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.BookShelf
import dev.alexjl.samazama.data.Book
import dev.alexjl.samazama.data.BookPage
import dev.alexjl.samazama.data.sampleBooks

@Composable
fun BookShelfScreen(
    viewModel: BookShelfViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(
        title = stringResource(viewModel.shelf.labelRes),
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        BookShelfContent(
            books = viewModel.books,
            onBookClick = onBookClick,
            onPageChange = viewModel::goToPage,
            onRetry = viewModel::retry
        )
    }
}

@Composable
private fun BookShelfContent(
    books: Result<BookPage>?,
    onBookClick: (Book) -> Unit,
    onPageChange: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (books) {
            null -> LoadingScreen(stringResource(R.string.loading_books))
            else -> books.fold(
                onSuccess = {
                    PagedBookList(
                        results = it,
                        emptyMessage = stringResource(R.string.no_books_on_shelf),
                        onBookClick = onBookClick,
                        onPageChange = onPageChange
                    )
                },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_load_books),
                        onRetry = onRetry
                    )
                }
            )
        }
    }
}

internal val BookShelf.labelRes: Int
    @StringRes get() = when (this) {
        BookShelf.READ -> R.string.count_read
        BookShelf.READING -> R.string.count_reading
        BookShelf.STACKED -> R.string.count_owned
        BookShelf.WISH -> R.string.count_wish
    }

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
private fun BookShelfContentPreview() {
    BookPreviewTheme {
        BookShelfContent(
            books = Result.success(
                BookPage(books = sampleBooks, page = 12, pageCount = 25, totalCount = 497)
            ),
            onBookClick = {},
            onPageChange = {},
            onRetry = {}
        )
    }
}
