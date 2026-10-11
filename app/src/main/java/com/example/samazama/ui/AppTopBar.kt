package com.example.samazama.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.samazama.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    sections: @Composable ColumnScope.() -> Unit = {}
) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.primaryContainer) {
        Column {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text(text = title) },
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.back)
                            )
                        }
                    }
                }
            )
            sections()
        }
    }
}

@Composable
internal fun AppScreen(
    title: String,
    onBackClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    topBarSections: @Composable ColumnScope.() -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(title = title, onBackClick = onBackClick, sections = topBarSections)
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}
