package com.example.samazama.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.samazama.R
import com.example.samazama.data.ThemeMode
import com.example.samazama.ui.theme.SamazamaTheme

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeader(stringResource(R.string.dark_mode))
        Column(modifier = Modifier.selectableGroup()) {
            ThemeMode.entries.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = option == themeMode,
                            onClick = { onThemeModeChange(option) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = option == themeMode, onClick = null)
                    Text(
                        text = stringResource(option.labelRes),
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}

private val ThemeMode.labelRes: Int
    @StringRes get() = when (this) {
        ThemeMode.SYSTEM -> R.string.dark_mode_system
        ThemeMode.LIGHT -> R.string.dark_mode_off
        ThemeMode.DARK -> R.string.dark_mode_on
    }

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun SettingsScreenPreview() {
    SamazamaTheme {
        SettingsScreen(themeMode = ThemeMode.SYSTEM, onThemeModeChange = {})
    }
}
