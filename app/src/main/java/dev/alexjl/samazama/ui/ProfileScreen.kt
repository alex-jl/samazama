package dev.alexjl.samazama.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.alexjl.samazama.R
import dev.alexjl.samazama.ui.theme.SamazamaTheme

@Composable
fun ProfileScreen(onBackClick: () -> Unit, modifier: Modifier = Modifier) = AppScreen(
    title = stringResource(R.string.profile),
    onBackClick = onBackClick,
    modifier = modifier
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.profile_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun ProfileScreenPreview() {
    SamazamaTheme {
        ProfileScreen(onBackClick = {})
    }
}
