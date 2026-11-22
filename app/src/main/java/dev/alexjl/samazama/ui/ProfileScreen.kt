package dev.alexjl.samazama.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.BookShelf
import dev.alexjl.samazama.data.UserProfile
import dev.alexjl.samazama.data.sampleUserProfile

@Composable
fun ProfileScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit,
    onLogInClick: () -> Unit,
    onShelfClick: (BookShelf) -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileContent(
        loggedIn = session.loggedIn,
        profile = session.profile,
        onBackClick = onBackClick,
        onLogInClick = onLogInClick,
        onShelfClick = onShelfClick,
        onRetry = session::refresh,
        modifier = modifier
    )
}

@Composable
private fun ProfileContent(
    loggedIn: Result<Boolean>?,
    profile: UserProfile?,
    onBackClick: () -> Unit,
    onLogInClick: () -> Unit,
    onShelfClick: (BookShelf) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) = AppScreen(
    title = stringResource(R.string.profile),
    onBackClick = onBackClick,
    modifier = modifier
) {
    when {
        loggedIn == null -> LoadingScreen(stringResource(R.string.checking_login))
        else -> loggedIn.fold(
            onSuccess = {
                if (it) {
                    LoggedInProfile(profile, onShelfClick)
                } else {
                    LoggedOutProfile(onLogInClick)
                }
            },
            onFailure = {
                ErrorScreen(
                    message = stringResource(R.string.failed_to_check_login),
                    onRetry = onRetry
                )
            }
        )
    }
}

@Composable
private fun LoggedInProfile(profile: UserProfile?, onShelfClick: (BookShelf) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (profile != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = profile.name,
                    modifier = Modifier.padding(start = 16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf(
                    BookShelf.READ to profile.readCount,
                    BookShelf.READING to profile.readingCount,
                    BookShelf.STACKED to profile.stackedCount,
                    BookShelf.WISH to profile.wishCount,
                ).forEach { (shelf, count) ->
                    BookCount(
                        labelRes = shelf.labelRes,
                        count = count,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onShelfClick(shelf) }
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                }
            }
        }
    }
}

@Composable
private fun LoggedOutProfile(onLogInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.logged_out_message),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onLogInClick) {
            Text(stringResource(R.string.log_in))
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun LoggedOutProfilePreview() {
    BookPreviewTheme {
        ProfileContent(Result.success(false), null, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun LoggedInProfilePreview() {
    BookPreviewTheme {
        ProfileContent(Result.success(true), sampleUserProfile, {}, {}, {}, {})
    }
}
