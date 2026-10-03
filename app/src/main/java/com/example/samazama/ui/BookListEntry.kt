package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks

private val EdgePadding = 16.dp
private val RankWidth = 40.dp
private val CoverWidth = 64.dp
private val CoverHeight = 96.dp
private val CoverTextGap = 16.dp

@Composable
fun BookListEntry(book: Book, modifier: Modifier = Modifier, displayIndex: Int? = null) {
    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(horizontal = EdgePadding, vertical = 12.dp)) {
            if (displayIndex != null) {
                Text(
                    text = displayIndex.toString(),
                    modifier = Modifier
                        .width(RankWidth)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFeatureSettings = "tnum"
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            AsyncImage(
                model = book.imageUrl,
                contentDescription = "Cover image for " + book.title,
                modifier = Modifier
                    .size(width = CoverWidth, height = CoverHeight)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = CoverTextGap)
            ) {
                Text(
                    text = book.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    text = book.author,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = EdgePadding))
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "BookListEntryPreviewDark"
)
@Composable
private fun BookListEntryPreview() {
    BookPreviewTheme {
        BookListEntry(sampleBooks.first())
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "NumberedBookListEntryPreviewDark"
)
@Composable
private fun NumberedBookListEntryPreview() {
    BookPreviewTheme {
        BookListEntry(sampleBooks.first(), displayIndex = 1)
    }
}
