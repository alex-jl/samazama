package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.samazama.R
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks
import com.example.samazama.icon.bookmarks
import java.text.NumberFormat

private val EdgePadding = 16.dp
private val RankWidth = 40.dp
private val CoverWidth = 64.dp
private val CoverHeight = 96.dp
private val CoverTextGap = 16.dp

/** Suffixes such as the " (新潮文庫 お 37-66)" of "幽冥の岸　十二国記 (新潮文庫 お 37-66)". */
private val IMPRINT_SUFFIX = Regex("\\s*\\([^()]*\\)\\s*$")

@Composable
fun BookListEntry(book: Book, modifier: Modifier = Modifier, displayIndex: Int? = null) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(horizontal = EdgePadding, vertical = 12.dp)
                .height(IntrinsicSize.Min)
        ) {
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
                    .fillMaxHeight()
                    .padding(start = CoverTextGap)
            ) {
                val suffixStart = IMPRINT_SUFFIX.find(book.title)?.range?.first
                Text(
                    text = if (suffixStart != null) book.title.substring(0, suffixStart) else book.title,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                if (suffixStart != null) {
                    Text(
                        text = book.title.substring(suffixStart).trim(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.author,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    if (book.registrationCount != null) {
                        RegistrationChip(
                            count = book.registrationCount,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = EdgePadding))
    }
}

@Composable
private fun RegistrationChip(count: Int, modifier: Modifier = Modifier) {
    val description = pluralStringResource(R.plurals.registration_count, count, count)
    Surface(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = bookmarks,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = NumberFormat.getIntegerInstance().format(count),
                modifier = Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
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
