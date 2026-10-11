package dev.alexjl.samazama.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.lazy.grid.LazyGridScope
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import dev.alexjl.samazama.R
import dev.alexjl.samazama.data.BookDetails
import dev.alexjl.samazama.data.BookEdition
import dev.alexjl.samazama.data.BookReview
import dev.alexjl.samazama.data.sampleBookDetails
import dev.alexjl.samazama.ui.icons.category
import dev.alexjl.samazama.ui.icons.enterprise
import dev.alexjl.samazama.ui.icons.open_in_new
import dev.alexjl.samazama.ui.icons.two_pager
import java.text.NumberFormat

private val EdgePadding = 16.dp
private val CardWidth = 220.dp
private val CardHeight = 64.dp
private val CardSpacing = 8.dp

private data class BookLink(val label: String, val url: String)

@Composable
fun BookDetailsScreen(
    viewModel: BookDetailsViewModel,
    onBackClick: () -> Unit,
    onEditionClick: (Int) -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AppScreen(
        title = stringResource(R.string.book_details),
        onBackClick = onBackClick,
        modifier = modifier
    ) {
        when (val result = viewModel.details) {
            null -> LoadingScreen(stringResource(R.string.loading_book))
            else -> result.fold(
                onSuccess = {
                    BookDetailsContent(
                        details = it,
                        reviews = viewModel.reviews,
                        hasMoreReviews = viewModel.hasMoreReviews,
                        loadingMoreReviews = viewModel.loadingMoreReviews,
                        onLoadMoreReviews = viewModel::loadMoreReviews,
                        onEditionClick = onEditionClick,
                        onAuthorClick = onAuthorClick
                    )
                },
                onFailure = {
                    ErrorScreen(
                        message = stringResource(R.string.failed_to_load_book),
                        onRetry = viewModel::retry
                    )
                }
            )
        }
    }
}

@Composable
private fun BookDetailsContent(
    details: BookDetails,
    reviews: List<BookReview>,
    hasMoreReviews: Boolean,
    loadingMoreReviews: Boolean,
    onLoadMoreReviews: () -> Unit,
    onEditionClick: (Int) -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getIntegerInstance() }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { BookHeader(details, onAuthorClick) }
        if (details.editions.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.other_editions)) }
            item { EditionGrid(details.editions, onEditionClick) }
        }
        if (details.trialUrl != null || details.stores.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.links)) }
            item {
                val sample = details.trialUrl?.let {
                    BookLink(stringResource(R.string.read_sample), it)
                }
                LinkGrid(listOfNotNull(sample) + details.stores.map { BookLink(it.name, it.url) })
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
private fun BookHeader(
    details: BookDetails,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
            val authors = details.author.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (authors.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    authors.forEach { author ->
                        Text(
                            text = author,
                            modifier = Modifier.clickable(
                                onClickLabel = stringResource(R.string.view_author),
                                onClick = { onAuthorClick(author) }
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
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
                if (details.pageCount != null && details.pageCount > 0) {
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
private fun LinkGrid(links: List<BookLink>, modifier: Modifier = Modifier) {
    CardGrid(itemCount = links.size, modifier = modifier) {
        items(links) { link -> LinkEntry(link) }
    }
}

@Composable
private fun LinkEntry(link: BookLink, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    OutlinedCard(
        onClick = { uriHandler.openUri(link.url) },
        modifier = modifier.width(CardWidth)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = link.label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Icon(
                imageVector = open_in_new,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EditionGrid(
    editions: List<BookEdition>,
    onEditionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    CardGrid(itemCount = editions.size, modifier = modifier) {
        items(editions, key = { it.id }) { edition ->
            EditionEntry(edition, onClick = { onEditionClick(edition.id) })
        }
    }
}

@Composable
private fun CardGrid(
    itemCount: Int,
    modifier: Modifier = Modifier,
    content: LazyGridScope.() -> Unit
) {
    val rows = minOf(itemCount, 2)
    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        modifier = modifier.height(CardHeight * rows + CardSpacing * (rows - 1)),
        contentPadding = PaddingValues(horizontal = EdgePadding),
        horizontalArrangement = Arrangement.spacedBy(CardSpacing),
        verticalArrangement = Arrangement.spacedBy(CardSpacing),
        content = content
    )
}

@Composable
private fun EditionEntry(edition: BookEdition, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(onClick = onClick, modifier = modifier.width(CardWidth)) {
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
            onEditionClick = {},
            onAuthorClick = {}
        )
    }
}
