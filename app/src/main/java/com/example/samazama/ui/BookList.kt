package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import coil3.annotation.ExperimentalCoilApi
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.example.samazama.R
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks
import com.example.samazama.ui.theme.SamazamaTheme

@Composable
fun BookList(
    books: List<Book>,
    modifier: Modifier = Modifier,
    numbered: Boolean = false
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items = books) { i, book ->
            BookListEntry(book = book, displayIndex = if (numbered) i + 1 else null)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun BookPreviewTheme(content: @Composable () -> Unit) {
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
        SamazamaTheme(content = content)
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "BookListPreviewDark"
)
@Composable
private fun BookListPreview() {
    BookPreviewTheme {
        BookList(sampleBooks)
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "NumberedBookListPreviewDark"
)
@Composable
private fun NumberedBookListPreview() {
    BookPreviewTheme {
        BookList(sampleBooks, numbered = true)
    }
}
