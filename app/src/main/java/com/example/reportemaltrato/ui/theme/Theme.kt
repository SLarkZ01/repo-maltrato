package com.example.reportemaltrato.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimaryLight,
    secondary = Secondary,
    onSecondary = OnPrimaryLight,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnPrimaryLight,
    tertiary = SuccessColor,
    onTertiary = OnPrimaryLight,
    tertiaryContainer = WarningColor,
    onTertiaryContainer = OnPrimaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLight,
    error = ErrorColor,
    onError = OnPrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimaryDark,
    secondary = Secondary,
    onSecondary = OnPrimaryDark,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnPrimaryDark,
    tertiary = SuccessColor,
    onTertiary = OnPrimaryDark,
    tertiaryContainer = WarningColor,
    onTertiaryContainer = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E6E6),
    surface = SurfaceDark,
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E6E6),
    surfaceVariant = SurfaceDark,
    error = ErrorColor,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun ReporteMaltratoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
