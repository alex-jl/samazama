package com.example.samazama.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samazama.R
import com.example.samazama.api.searchBooks
import com.example.samazama.data.BookSearchPage
import com.example.samazama.data.sampleBooks
import com.example.samazama.icon.close
import com.example.samazama.icon.search
import kotlinx.coroutines.CancellationException

@Composable
fun SearchScreen(keyword: String?, submission: Int, modifier: Modifier = Modifier) {
    var page by rememberSaveable(keyword, submission) { mutableIntStateOf(1) }
    var retryCount by rememberSaveable { mutableIntStateOf(0) }
    val results by produceState<Result<BookSearchPage>?>(
        initialValue = null, keyword, submission, page, retryCount
    ) {
        value = null
        if (keyword == null) return@produceState
        value = try {
            Result.success(searchBooks(keyword, page))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SearchScreen", "Failed to search: ${e::class.qualifiedName}: ${e.message}")
            Result.failure(e)
        }
    }

    SearchContent(
        hasSearched = keyword != null,
        results = results,
        onPageChange = { page = it },
        onRetry = { retryCount++ },
        modifier = modifier
    )
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(close, stringResource(R.string.clear))
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
                onSearch()
            }
        ),
        singleLine = true,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun SearchContent(
    hasSearched: Boolean,
    results: Result<BookSearchPage>?,
    onPageChange: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            !hasSearched -> Unit
            results == null -> LoadingScreen(stringResource(R.string.searching))
            else -> results.fold(
                onSuccess = { SearchResults(it, onPageChange) },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_search),
                        onRetry = onRetry
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchResults(
    results: BookSearchPage,
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
                    text = stringResource(R.string.no_results),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            BookList(books = results.books, modifier = Modifier.weight(1f))
        }
        if (results.pageCount > 1 || results.page > 1) {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onPageChange(results.page - 1) },
                    modifier = Modifier.size(40.dp),
                    enabled = results.page > 1
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        stringResource(R.string.previous_page)
                    )
                }
                for (number in 1..results.pageCount) {
                    val description =
                        stringResource(R.string.page_indicator, number, results.pageCount)
                    val pageModifier = Modifier
                        .size(40.dp)
                        .semantics { contentDescription = description }
                    if (number == results.page) {
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
                    onClick = { onPageChange(results.page + 1) },
                    modifier = Modifier.size(40.dp),
                    enabled = results.hasNextPage
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        stringResource(R.string.next_page)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
private fun SearchScreenPreview() {
    BookPreviewTheme {
        SearchContent(
            hasSearched = true,
            results = Result.success(
                BookSearchPage(books = sampleBooks, page = 2, pageCount = 3, totalCount = 51)
            ),
            onPageChange = {},
            onRetry = {}
        )
    }
}
