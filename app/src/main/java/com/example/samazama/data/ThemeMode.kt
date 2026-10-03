package com.example.samazama.data

import android.content.Context
import androidx.core.content.edit

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

private const val SETTINGS_PREFS = "settings"
private const val THEME_MODE_KEY = "theme_mode"

fun loadThemeMode(context: Context): ThemeMode {
    val saved = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
        .getString(THEME_MODE_KEY, null)
    return ThemeMode.entries.firstOrNull { it.name == saved } ?: ThemeMode.SYSTEM
}

fun saveThemeMode(context: Context, mode: ThemeMode) {
    context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE).edit {
        putString(THEME_MODE_KEY, mode.name)
    }
}
