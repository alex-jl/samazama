package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks

@Composable
fun BookListEntry(book: Book, modifier: Modifier = Modifier, displayIndex: Int? = null) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (displayIndex != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayIndex.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                    ),
                )
            }
        }
        OutlinedCard(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            elevation = CardDefaults.elevatedCardElevation(4.dp)
        ) {
            BookListEntryContent(book)
        }
    }
}

@Composable
private fun BookListEntryContent(book: Book, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                text = book.displayTitle,
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
