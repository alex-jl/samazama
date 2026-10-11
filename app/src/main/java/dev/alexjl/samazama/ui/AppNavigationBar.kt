package dev.alexjl.samazama.ui

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import dev.alexjl.samazama.R
import dev.alexjl.samazama.ui.icons.home
import dev.alexjl.samazama.ui.icons.person
import dev.alexjl.samazama.ui.icons.search
import dev.alexjl.samazama.ui.icons.settings

enum class NavTab(@StringRes internal val labelRes: Int) {
    HOME(R.string.home),
    PROFILE(R.string.profile),
    SEARCH(R.string.search),
    SETTINGS(R.string.settings),
}

private val NavTab.icon: ImageVector
    get() = when (this) {
        NavTab.HOME -> home
        NavTab.PROFILE -> person
        NavTab.SEARCH -> search
        NavTab.SETTINGS -> settings
    }

@Composable
fun AppNavigationBar(
    selected: NavTab?,
    onSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier, windowInsets = NavigationBarDefaults.windowInsets) {
        NavTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = {
                    Icon(tab.icon, stringResource(tab.labelRes))
                },
                label = { Text(stringResource(tab.labelRes)) }
            )
        }
    }
}
