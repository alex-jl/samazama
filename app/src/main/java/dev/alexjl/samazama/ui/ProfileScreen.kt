package dev.alexjl.samazama.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexjl.samazama.R
import dev.alexjl.samazama.ui.theme.SamazamaTheme

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
                    LoginStatus(stringResource(R.string.logged_in_message)) {
                        Button(onClick = onAccountClick) {
                            Text(stringResource(R.string.account))
                        }
                        OutlinedButton(onClick = onLogOutClick) {
                            Text(stringResource(R.string.log_out))
                        }
                        if (logOutFailed) {
                            Text(
                                text = stringResource(R.string.failed_to_log_out),
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LoginStatus(stringResource(R.string.logged_out_message)) {
                        Button(onClick = onLogInClick) {
                            Text(stringResource(R.string.log_in))
                        }
                    }
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
private fun LoginStatus(message: String, actions: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        actions()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun LoggedOutProfilePreview() {
    SamazamaTheme {
        ProfileContent(Result.success(false), false, false, {}, {}, {}, {}, {})
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun LoggedInProfilePreview() {
    SamazamaTheme {
        ProfileContent(Result.success(true), false, true, {}, {}, {}, {}, {})
    }
}
