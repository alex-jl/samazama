package com.example.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.annotation.PluralsRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.samazama.R
import com.example.samazama.api.fetchBookDetails
import com.example.samazama.api.fetchBookReviews
import com.example.samazama.data.BookDetails
import com.example.samazama.data.BookEdition
import com.example.samazama.data.BookReview
import com.example.samazama.data.sampleBookDetails
import com.example.samazama.ui.icons.category
import com.example.samazama.ui.icons.enterprise
import com.example.samazama.ui.icons.open_in_new
import com.example.samazama.ui.icons.two_pager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.text.NumberFormat

private val EdgePadding = 16.dp
private val EditionWidth = 220.dp
private val EditionHeight = 64.dp
private val EditionSpacing = 8.dp

@Composable
fun BookDetailsScreen(
    bookId: Int,
    onEditionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var retryCount by rememberSaveable { mutableIntStateOf(0) }
    val details by produceState<Result<BookDetails>?>(initialValue = null, bookId, retryCount) {
        value = null
        value = try {
            Result.success(fetchBookDetails(bookId))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("BookDetailsScreen", "Failed to load book: ${e::class.qualifiedName}: ${e.message}")
            Result.failure(e)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val result = details) {
            null -> LoadingScreen(stringResource(R.string.loading_book))
            else -> result.fold(
                onSuccess = { LoadedBookDetails(it, onEditionClick) },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_load_book),
                        onRetry = { retryCount++ }
                    )
                }
            )
        }
    }
}

@Composable
private fun LoadedBookDetails(details: BookDetails, onEditionClick: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    var reviews by remember(details) { mutableStateOf(details.reviews.reviews) }
    var lastPage by remember(details) { mutableStateOf(details.reviews) }
    var loadingMore by remember(details) { mutableStateOf(false) }

    BookDetailsContent(
        details = details,
        reviews = reviews,
        hasMoreReviews = lastPage.hasNextPage,
        loadingMoreReviews = loadingMore,
        onLoadMoreReviews = {
            loadingMore = true
            scope.launch {
                try {
                    val page = fetchBookReviews(
                        details.id,
                        offset = lastPage.offset + lastPage.reviews.size
                    )
                    val known = reviews.mapTo(HashSet()) { it.id }
                    reviews = reviews + page.reviews.filter { it.id !in known }
                    lastPage = page
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(
                        "BookDetailsScreen",
                        "Failed to load reviews: ${e::class.qualifiedName}: ${e.message}"
                    )
                } finally {
                    loadingMore = false
                }
            }
        },
        onEditionClick = onEditionClick
    )
}

@Composable
private fun BookDetailsContent(
    details: BookDetails,
    reviews: List<BookReview>,
    hasMoreReviews: Boolean,
    loadingMoreReviews: Boolean,
    onLoadMoreReviews: () -> Unit,
    onEditionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getIntegerInstance() }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { BookHeader(details) }
        if (details.editions.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.other_editions)) }
            item { EditionGrid(details.editions, onEditionClick) }
        }
        if (details.trialUrl != null) {
            item { SectionHeader(stringResource(R.string.links)) }
            item {
                LinkEntry(
                    label = stringResource(R.string.read_sample),
                    url = details.trialUrl
                )
            }
        }
        item {
            val reviewCount = details.reviewCount ?: details.reviews.totalCount
            SectionHeader(
                stringResource(R.string.reviews_with_count, numberFormat.format(reviewCount))
            )
        }
        if (reviews.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_reviews),
                    modifier = Modifier.padding(horizontal = EdgePadding, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(reviews, key = { "review-${it.id}" }) { review ->
            ReviewEntry(review)
        }
        if (hasMoreReviews) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (loadingMoreReviews) {
                        CircularProgressIndicator(modifier = Modifier.size(32.dp))
                    } else {
                        TextButton(onClick = onLoadMoreReviews) {
                            Text(stringResource(R.string.show_more))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BookHeader(details: BookDetails, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(EdgePadding)) {
        AsyncImage(
            model = details.imageUrl,
            contentDescription = "Cover image for " + details.title,
            modifier = Modifier
                .size(width = 96.dp, height = 144.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            val suffixStart = IMPRINT_SUFFIX.find(details.title)?.range?.first
            Text(
                text = if (suffixStart != null) details.title.substring(0, suffixStart) else details.title,
                style = MaterialTheme.typography.titleLarge
            )
            if (suffixStart != null) {
                Text(
                    text = details.title.substring(suffixStart).trim(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (details.author.isNotEmpty()) {
                Text(
                    text = details.author,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            FlowRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (details.format != null) {
                    InfoChip(details.format, icon = category)
                }
                if (details.publisher != null) {
                    InfoChip(details.publisher, icon = enterprise)
                }
                if (details.pageCount != null) {
                    InfoChip(
                        text = pluralStringResource(
                            R.plurals.page_count,
                            details.pageCount,
                            details.pageCount
                        ),
                        icon = two_pager
                    )
                }
                if (details.registrationCount != null) {
                    RegistrationChip(count = details.registrationCount)
                }
            }
        }
    }
}

@Composable
private fun LinkEntry(label: String, url: String, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    ListItem(
        headlineContent = { Text(label) },
        modifier = modifier.clickable { uriHandler.openUri(url) },
        trailingContent = { Icon(open_in_new, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun EditionGrid(
    editions: List<BookEdition>,
    onEditionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = minOf(editions.size, 2)
    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        modifier = modifier.height(EditionHeight * rows + EditionSpacing * (rows - 1)),
        contentPadding = PaddingValues(horizontal = EdgePadding),
        horizontalArrangement = Arrangement.spacedBy(EditionSpacing),
        verticalArrangement = Arrangement.spacedBy(EditionSpacing)
    ) {
        items(editions, key = { it.id }) { edition ->
            EditionEntry(edition, onClick = { onEditionClick(edition.id) })
        }
    }
}

@Composable
private fun EditionEntry(edition: BookEdition, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(onClick = onClick, modifier = modifier.width(EditionWidth)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = edition.format ?: stringResource(R.string.unknown_format),
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                if (edition.publisher != null) {
                    Text(
                        text = edition.publisher,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            if (edition.registrationCount != null) {
                RegistrationChip(
                    count = edition.registrationCount,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ReviewEntry(review: BookReview, modifier: Modifier = Modifier) {
    var revealed by rememberSaveable { mutableStateOf(!review.isSpoiler) }

    Column(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = EdgePadding, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.reviewer.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = review.reviewer.name,
                        style = MaterialTheme.typography.titleSmall,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Text(
                        text = review.createdAt,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (revealed) {
                Text(
                    text = review.content,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.spoiler_hidden),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = { revealed = true }) {
                        Text(stringResource(R.string.show_spoiler))
                    }
                }
            }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReviewCount(Icons.Outlined.ThumbUp, review.niceCount, R.plurals.nice_count)
                ReviewCount(
                    Icons.AutoMirrored.Outlined.Comment,
                    review.commentCount,
                    R.plurals.comment_count
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = EdgePadding))
    }
}

@Composable
private fun ReviewCount(
    icon: ImageVector,
    count: Int,
    @PluralsRes descriptionRes: Int,
    modifier: Modifier = Modifier
) {
    val description = pluralStringResource(descriptionRes, count, count)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = NumberFormat.getIntegerInstance().format(count),
            modifier = Modifier.padding(start = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 720)
@Preview(
    showBackground = true,
    widthDp = 320,
    heightDp = 720,
    uiMode = UI_MODE_NIGHT_YES,
    name = "BookDetailsContentPreviewDark"
)
@Composable
private fun BookDetailsContentPreview() {
    BookPreviewTheme {
        BookDetailsContent(
            details = sampleBookDetails,
            reviews = sampleBookDetails.reviews.reviews,
            hasMoreReviews = true,
            loadingMoreReviews = false,
            onLoadMoreReviews = {},
            onEditionClick = {}
        )
    }
}
