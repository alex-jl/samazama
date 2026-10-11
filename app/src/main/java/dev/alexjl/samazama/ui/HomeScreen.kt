package dev.alexjl.samazama.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.BOOKMETER_BASE_URL
import dev.alexjl.samazama.api.DEFAULT_PROFILE_IMAGE_URL
import dev.alexjl.samazama.data.UserProfile
import dev.alexjl.samazama.data.sampleUserProfile
import dev.alexjl.samazama.ui.icons.chevron_right
import dev.alexjl.samazama.ui.icons.open_in_new
import java.text.NumberFormat

private val ProfilePictureSize = 72.dp
private val ProfileSummaryHeight = 160.dp

private val PlaceholderProfile =
    UserProfile(id = 0, name = "", imageUrl = DEFAULT_PROFILE_IMAGE_URL)

@Composable
fun HomeScreen(
    loggedIn: Result<Boolean>?,
    profile: UserProfile?,
    onProfileClick: () -> Unit,
    onLogInClick: () -> Unit,
    onRetryProfile: () -> Unit,
    onRankingsClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) = AppScreen(
    title = stringResource(R.string.home),
    onBackClick = null,
    modifier = modifier
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when {
            loggedIn == null -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ProfileSummaryHeight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            loggedIn.isFailure -> ProfilePlaceholder(
                message = stringResource(R.string.failed_to_load_profile),
                actionLabel = stringResource(R.string.retry),
                onAction = onRetryProfile
            )
            profile != null -> ProfileSummary(profile, onClick = onProfileClick)
            else -> ProfilePlaceholder(
                message = stringResource(R.string.log_in_prompt),
                actionLabel = stringResource(R.string.log_in),
                onAction = onLogInClick
            )
        }
        SectionHeader(stringResource(R.string.discover))
        SectionLink(stringResource(R.string.rankings), onClick = onRankingsClick)
        SectionLink(stringResource(R.string.search), onClick = onSearchClick)
        SectionHeader(stringResource(R.string.other))
        SectionLink(
            label = stringResource(R.string.visit_bookmeter),
            onClick = { uriHandler.openUri(BOOKMETER_BASE_URL) },
            icon = open_in_new
        )
        Text(
            text = stringResource(R.string.about_unofficial, stringResource(R.string.app_name)),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ProfilePlaceholder(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ProfileSummary(
            profile = PlaceholderProfile,
            onClick = null,
            modifier = Modifier
                .alpha(0.25f)
                .clearAndSetSemantics {}
        )
        Surface(
            modifier = Modifier.padding(horizontal = 32.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onAction, modifier = Modifier.padding(top = 8.dp)) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
private fun ProfileSummary(
    profile: UserProfile,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val profileLabel = stringResource(R.string.profile)
    Row(
        modifier = modifier
            .heightIn(min = ProfileSummaryHeight)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .width(ProfilePictureSize + 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClickLabel = profileLabel, onClick = onClick)
                    } else {
                        Modifier
                    }
                )
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = profile.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(ProfilePictureSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Text(
                text = profile.name,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val cardModifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BookCountCard(R.string.count_read, profile.readCount, cardModifier)
                BookCountCard(R.string.count_reading, profile.readingCount, cardModifier)
            }
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BookCountCard(R.string.count_owned, profile.stackedCount, cardModifier)
                BookCountCard(R.string.count_wish, profile.wishCount, cardModifier)
            }
        }
    }
}

@Composable
private fun BookCountCard(@StringRes labelRes: Int, count: Int?, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(
                text = count?.let { NumberFormat.getIntegerInstance().format(it) } ?: "–",
                style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
                maxLines = 1
            )
            Text(
                text = stringResource(labelRes),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }
}

@Composable
internal fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
private fun SectionLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = chevron_right
) {
    ListItem(
        headlineContent = { Text(label) },
        modifier = modifier.clickable(onClick = onClick),
        trailingContent = { Icon(icon, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
private fun HomeScreenPreview() {
    BookPreviewTheme {
        HomeScreen(
            loggedIn = Result.success(true),
            profile = sampleUserProfile,
            onProfileClick = {},
            onLogInClick = {},
            onRetryProfile = {},
            onRankingsClick = {},
            onSearchClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
private fun LoggedOutHomeScreenPreview() {
    BookPreviewTheme {
        HomeScreen(
            loggedIn = Result.success(false),
            profile = null,
            onProfileClick = {},
            onLogInClick = {},
            onRetryProfile = {},
            onRankingsClick = {},
            onSearchClick = {}
        )
    }
}
