package com.linkpostr.app.ui.theme

import android.app.Activity
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.linkpostr.app.ui.AppThemeOption

private fun midnightScheme(): ColorScheme = darkColorScheme(
    primary = MidnightPrimary,
    onPrimary = Color.White,
    secondary = MidnightSecondary,
    onSecondary = Color(0xFF03101F),
    background = MidnightBackground,
    onBackground = MidnightText,
    surface = MidnightSurface,
    onSurface = MidnightText,
    onSurfaceVariant = MidnightMuted,
    outline = MidnightOutline,
)

private fun electricScheme(): ColorScheme = darkColorScheme(
    primary = ElectricPrimary,
    onPrimary = Color(0xFF02131A),
    secondary = ElectricSecondary,
    onSecondary = Color.White,
    background = ElectricBackground,
    onBackground = ElectricText,
    surface = ElectricSurface,
    onSurface = ElectricText,
    onSurfaceVariant = ElectricMuted,
    outline = ElectricOutline,
)

private fun oceanScheme(): ColorScheme = lightColorScheme(
    primary = OceanPrimary,
    onPrimary = Color.White,
    secondary = OceanSecondary,
    onSecondary = Color.White,
    background = OceanBackground,
    onBackground = OceanText,
    surface = OceanSurface,
    onSurface = OceanText,
    onSurfaceVariant = OceanMuted,
    outline = OceanOutline,
)

private fun cloudScheme(): ColorScheme = lightColorScheme(
    primary = CloudPrimary,
    onPrimary = Color.White,
    secondary = CloudSecondary,
    onSecondary = Color.White,
    background = CloudBackground,
    onBackground = CloudText,
    surface = CloudSurface,
    onSurface = CloudText,
    onSurfaceVariant = CloudMuted,
    outline = CloudOutline,
)

@Composable
fun LinkPostrTheme(
    themeOption: AppThemeOption,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeOption) {
        AppThemeOption.MidnightBlue -> midnightScheme()
        AppThemeOption.ElectricNight -> electricScheme()
        AppThemeOption.OceanLight -> oceanScheme()
        AppThemeOption.CloudLight -> cloudScheme()
    }
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !themeOption.isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LinkPostrTypography,
        content = content,
    )
}
