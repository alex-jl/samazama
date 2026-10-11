package dev.alexjl.samazama.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import dev.alexjl.samazama.data.UserProfile
import dev.alexjl.samazama.data.sampleUserProfile
import java.text.NumberFormat

@Composable
fun ProfileScreen(
    session: SessionViewModel,
    onBackClick: () -> Unit,
    onAccountClick: () -> Unit,
    onLogInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileContent(
        loggedIn = session.loggedIn,
        profile = session.profile,
        loggingOut = session.loggingOut,
        logOutFailed = session.logOutFailed,
        onBackClick = onBackClick,
        onAccountClick = onAccountClick,
        onLogInClick = onLogInClick,
        onLogOutClick = session::logOut,
        onRetry = session::refresh,
        modifier = modifier
    )
}

@Composable
private fun ProfileContent(
    loggedIn: Result<Boolean>?,
    profile: UserProfile?,
    loggingOut: Boolean,
    logOutFailed: Boolean,
    onBackClick: () -> Unit,
    onAccountClick: () -> Unit,
    onLogInClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) = AppScreen(
    title = stringResource(R.string.profile),
    onBackClick = onBackClick,
    modifier = modifier
) {
    when {
        loggingOut -> LoadingScreen(stringResource(R.string.logging_out))
        loggedIn == null -> LoadingScreen(stringResource(R.string.checking_login))
        else -> loggedIn.fold(
            onSuccess = {
                if (it) {
                    LoggedInProfile(profile, logOutFailed, onAccountClick, onLogOutClick)
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
private fun LoggedInProfile(
    profile: UserProfile?,
    logOutFailed: Boolean,
    onAccountClick: () -> Unit,
    onLogOutClick: () -> Unit
) {
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
                BookCount(R.string.count_read, profile.readCount, Modifier.weight(1f))
                BookCount(R.string.count_reading, profile.readingCount, Modifier.weight(1f))
                BookCount(R.string.count_owned, profile.stackedCount, Modifier.weight(1f))
                BookCount(R.string.count_wish, profile.wishCount, Modifier.weight(1f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAccountClick) {
                Text(stringResource(R.string.account))
            }
            OutlinedButton(onClick = onLogOutClick) {
                Text(stringResource(R.string.log_out))
            }
        }
        if (logOutFailed) {
            Text(
                text = stringResource(R.string.failed_to_log_out),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun BookCount(@StringRes labelRes: Int, count: Int?, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count?.let { NumberFormat.getIntegerInstance().format(it) } ?: "–",
            style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
            maxLines = 1
        )
        Text(
            text = stringResource(labelRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center
        )
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
        ProfileContent(Result.success(false), null, false, false, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun LoggedInProfilePreview() {
    BookPreviewTheme {
        ProfileContent(Result.success(true), sampleUserProfile, false, true, {}, {}, {}, {}, {})
    }
}
