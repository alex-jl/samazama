package dev.alexjl.samazama.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.NotLoggedInException
import dev.alexjl.samazama.data.AccountDetails
import dev.alexjl.samazama.data.AccountField
import dev.alexjl.samazama.data.sampleAccountDetails
import dev.alexjl.samazama.ui.theme.SamazamaTheme

@Composable
fun AccountScreen(
    viewModel: AccountViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AccountContent(
        details = viewModel.details,
        onBackClick = onBackClick,
        onRetry = viewModel::retry,
        modifier = modifier
    )
}

@Composable
private fun AccountContent(
    details: Result<AccountDetails>?,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) = AppScreen(
    title = stringResource(R.string.account),
    onBackClick = onBackClick,
    modifier = modifier
) {
    when (details) {
        null -> LoadingScreen(stringResource(R.string.loading_account))
        else -> details.fold(
            onSuccess = { AccountFields(it) },
            onFailure = {
                ErrorScreen(
                    message = stringResource(
                        if (it is NotLoggedInException) {
                            R.string.account_not_logged_in
                        } else {
                            R.string.failed_to_load_account
                        }
                    ),
                    onRetry = onRetry
                )
            }
        )
    }
}

@Composable
private fun AccountFields(details: AccountDetails, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Text(
                text = stringResource(R.string.account_logged_in),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
            )
        }
        item {
            Text(
                text = details.pageTitle,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (details.fields.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.account_no_fields),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(details.fields) { field -> AccountFieldEntry(field) }
    }
}

@Composable
private fun AccountFieldEntry(field: AccountField, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = field.label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium
        )
        Text(text = field.value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun AccountContentPreview() {
    SamazamaTheme {
        AccountContent(Result.success(sampleAccountDetails), onBackClick = {}, onRetry = {})
    }
}
