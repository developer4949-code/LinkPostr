package com.linkpostr.app.data

import android.content.Context
import com.linkpostr.app.ui.AppThemeOption

class ThemePreferences(context: Context) {

    private val preferences = context.getSharedPreferences("linkpostr_preferences", Context.MODE_PRIVATE)

    fun getSelectedTheme(): AppThemeOption {
        val savedName = preferences.getString(KEY_THEME, AppThemeOption.MidnightBlue.name)
        return AppThemeOption.entries.firstOrNull { it.name == savedName } ?: AppThemeOption.MidnightBlue
    }

    fun saveSelectedTheme(theme: AppThemeOption) {
        preferences.edit().putString(KEY_THEME, theme.name).apply()
    }

    private companion object {
        const val KEY_THEME = "selected_theme"
    }
}
