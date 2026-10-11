package dev.alexjl.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.BookFormat
import dev.alexjl.samazama.api.RankingPeriod
import dev.alexjl.samazama.data.Book
import dev.alexjl.samazama.data.sampleBooks

@Composable
fun BookRankingScreen(
    viewModel: BookRankingViewModel,
    onBackClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(
        title = stringResource(R.string.rankings),
        onBackClick = onBackClick,
        modifier = modifier,
        topBarSections = {
            RankingFilters(
                format = viewModel.format,
                period = viewModel.period,
                onFormatChange = viewModel::selectFormat,
                onPeriodChange = viewModel::selectPeriod
            )
        }
    ) {
        BookRankingContent(
            rankings = viewModel.rankings,
            onBookClick = onBookClick,
            onRetry = viewModel::retry
        )
    }
}

@Composable
internal fun LoadingScreen(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Text(
                text = message,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
internal fun ErrorScreen(message: String, modifier: Modifier = Modifier, onRetry: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message)
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
    rankings: Result<List<Book>>?,
    onBookClick: (Book) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (rankings) {
            null -> LoadingScreen(stringResource(R.string.loading_rankings))
            else -> rankings.fold(
                onSuccess = { books ->
                    BookList(books = books, onBookClick = onBookClick, numbered = true)
                },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_load_rankings),
                        onRetry = onRetry
                    )
                }
            )
        }
    }
}

@Composable
fun RankingFilters(
    format: BookFormat,
    period: RankingPeriod,
    onFormatChange: (BookFormat) -> Unit,
    onPeriodChange: (RankingPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
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
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false,
                borderColor = MaterialTheme.colorScheme.outline
            )
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
            rankings = Result.success(sampleBooks),
            onBookClick = {},
            onRetry = {}
        )
    }
}
