package com.example.samazama.ui

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
import com.example.samazama.R
import com.example.samazama.icon.home
import com.example.samazama.icon.search
import com.example.samazama.icon.settings

enum class NavTab(@StringRes internal val labelRes: Int) {
    HOME(R.string.home),
    SEARCH(R.string.search),
    SETTINGS(R.string.settings),
}

private val NavTab.icon: ImageVector
    get() = when (this) {
        NavTab.HOME -> home
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
