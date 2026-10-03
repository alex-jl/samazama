package com.example.samazama.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.samazama.R
import com.example.samazama.icon.home
import com.example.samazama.icon.search
import com.example.samazama.icon.settings

@Composable
fun AppNavigationBar(
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
    homeSelected: Boolean = false
) {
    NavigationBar(modifier = modifier, windowInsets = NavigationBarDefaults.windowInsets) {
        NavigationBarItem(
            selected = homeSelected,
            onClick = onHomeClick,
            icon = {
                Icon(home, stringResource(R.string.home))
            },
            label = { Text(stringResource(R.string.home)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { 0 },
            icon = {
                Icon(search, stringResource(R.string.search))
            },
            label = { Text(stringResource(R.string.search)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { 0 },
            icon = {
                Icon(settings, stringResource(R.string.settings))
            },
            label = { Text(stringResource(R.string.settings)) }
        )
    }
}
