package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samazama.R
import com.example.samazama.api.BookFormat
import com.example.samazama.api.RankingPeriod
import com.example.samazama.api.fetchRankings
import com.example.samazama.data.Book
import com.example.samazama.data.sampleBooks
import kotlinx.coroutines.CancellationException

@Composable
fun BookRankingScreen(modifier: Modifier = Modifier) {
    var format by rememberSaveable { mutableStateOf(BookFormat.BUNKO) }
    var period by rememberSaveable { mutableStateOf(RankingPeriod.MONTH) }
    var retryCount by rememberSaveable { mutableIntStateOf(0) }
    val rankings by produceState<Result<List<Book>>?>(initialValue = null, format, period, retryCount) {
        value = null
        value = try {
            Result.success(fetchRankings(format = format, period = period))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("BookRankingScreen", "Failed to load rankings: ${e::class.qualifiedName}: ${e.message}")
            Result.failure(e)
        }
    }

    BookRankingContent(
        format = format,
        period = period,
        rankings = rankings,
        onFormatChange = { format = it },
        onPeriodChange = { period = it },
        onRetry = { retryCount++ },
        modifier = modifier
    )
}

@Composable
private fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_rankings),
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun ErrorScreen(modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(R.string.failed_to_load_rankings))
            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .wrapContentSize(),
                onClick = onRetry
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun BookRankingContent(
    format: BookFormat,
    period: RankingPeriod,
    rankings: Result<List<Book>>?,
    onFormatChange: (BookFormat) -> Unit,
    onPeriodChange: (RankingPeriod) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        RankingFilters(
            format = format,
            period = period,
            onFormatChange = onFormatChange,
            onPeriodChange = onPeriodChange
        )
        when (rankings) {
            null -> LoadingScreen()
            else -> rankings.fold(
                onSuccess = { books -> BookList(books = books, numbered = true) },
                onFailure = { ErrorScreen(onRetry = onRetry) }
            )
        }
    }
}

@Composable
private fun RankingFilters(
    format: BookFormat,
    period: RankingPeriod,
    onFormatChange: (BookFormat) -> Unit,
    onPeriodChange: (RankingPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DropdownChip(BookFormat.entries, format, onFormatChange) { stringResource(it.labelRes) }
        DropdownChip(RankingPeriod.entries, period, onPeriodChange) { stringResource(it.labelRes) }
    }
}

@Composable
private fun <T> DropdownChip(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        FilterChip(
            selected = false,
            onClick = { expanded = true },
            label = { Text(label(selected)) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(label(option)) },
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }
        }
    }
}

private val BookFormat.labelRes: Int
    @StringRes get() = when (this) {
        BookFormat.BUNKO -> R.string.bunko
        BookFormat.TANKOUBON -> R.string.tankoubon
        BookFormat.COMIC -> R.string.comic
        BookFormat.LIGHT_NOVEL -> R.string.light_novel
        BookFormat.OTHERS -> R.string.other
    }

private val RankingPeriod.labelRes: Int
    @StringRes get() = when (this) {
        RankingPeriod.DAY -> R.string.period_day
        RankingPeriod.WEEK -> R.string.period_week
        RankingPeriod.MONTH -> R.string.period_month
    }

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "BookRankingContentPreviewDark"
)
@Composable
private fun BookRankingContentPreview() {
    BookPreviewTheme {
        BookRankingContent(
            format = BookFormat.BUNKO,
            period = RankingPeriod.MONTH,
            rankings = Result.success(sampleBooks),
            onFormatChange = {},
            onPeriodChange = {},
            onRetry = {}
        )
    }
}
