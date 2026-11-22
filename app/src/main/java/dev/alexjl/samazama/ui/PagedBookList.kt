package dev.alexjl.samazama.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.alexjl.samazama.R
import dev.alexjl.samazama.data.Book
import dev.alexjl.samazama.data.BookPage

private const val VISIBLE_PAGE_NUMBERS = 5

@Composable
internal fun PagedBookList(
    results: BookPage,
    emptyMessage: String,
    onBookClick: (Book) -> Unit,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (results.books.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emptyMessage,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            BookList(
                books = results.books,
                onBookClick = onBookClick,
                modifier = Modifier.weight(1f)
            )
        }
        val paged = results.pageCount > 1 || results.page > 1
        if (results.books.isNotEmpty() || paged) {
            HorizontalDivider()
        }
        if (results.books.isNotEmpty()) {
            BookCountSummary(
                results = results,
                modifier = Modifier.padding(top = 8.dp, bottom = if (paged) 0.dp else 8.dp)
            )
        }
        if (paged) {
            PageControls(results.page, results.pageCount, onPageChange)
        }
    }
}

@Composable
private fun BookCountSummary(results: BookPage, modifier: Modifier = Modifier) {
    val total = results.totalCount
    val text = if (results.pageCount > 1) {
        val first = if (results.hasNextPage) {
            (results.page - 1) * results.books.size + 1
        } else {
            total - results.books.size + 1
        }
        pluralStringResource(
            R.plurals.book_range,
            total,
            first,
            first + results.books.size - 1,
            total
        )
    } else {
        pluralStringResource(R.plurals.book_count, total, total)
    }
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodySmall,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun PageControls(
    page: Int,
    pageCount: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val first = (page - VISIBLE_PAGE_NUMBERS / 2)
        .coerceAtMost(pageCount - VISIBLE_PAGE_NUMBERS + 1)
        .coerceAtLeast(1)
    val last = (first + VISIBLE_PAGE_NUMBERS - 1).coerceAtMost(pageCount)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageChange(page - 1) },
            modifier = Modifier.size(40.dp),
            enabled = page > 1
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                stringResource(R.string.previous_page)
            )
        }
        for (number in first..last) {
            val description = stringResource(R.string.page_indicator, number, pageCount)
            val pageModifier = Modifier
                .size(40.dp)
                .semantics { contentDescription = description }
            if (number == page) {
                FilledTonalIconButton(onClick = {}, modifier = pageModifier) {
                    Text(number.toString())
                }
            } else {
                IconButton(onClick = { onPageChange(number) }, modifier = pageModifier) {
                    Text(number.toString())
                }
            }
        }
        IconButton(
            onClick = { onPageChange(page + 1) },
            modifier = Modifier.size(40.dp),
            enabled = page < pageCount
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                stringResource(R.string.next_page)
            )
        }
    }
}
