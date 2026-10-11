package dev.alexjl.samazama.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexjl.samazama.R
import dev.alexjl.samazama.api.BOOKMETER_BASE_URL
import dev.alexjl.samazama.ui.icons.chevron_right
import dev.alexjl.samazama.ui.icons.open_in_new
import dev.alexjl.samazama.ui.theme.SamazamaTheme

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit,
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
        SectionHeader(stringResource(R.string.my_books))
        SectionLink(stringResource(R.string.profile), onClick = onProfileClick)
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

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun HomeScreenPreview() {
    SamazamaTheme {
        HomeScreen(onProfileClick = {}, onRankingsClick = {}, onSearchClick = {})
    }
}
