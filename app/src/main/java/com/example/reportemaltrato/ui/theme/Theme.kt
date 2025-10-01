package com.example.reportemaltrato.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette: Colors = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary,
    secondaryVariant = SecondaryVariant,
    background = BackgroundLight,
    surface = SurfaceLight,
    error = ErrorColor,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color(0xFF1B1F23),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1B1F23),
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkColorPalette: Colors = darkColors(
    primary = PrimaryDark,
    primaryVariant = Primary,
    secondary = Secondary,
    background = BackgroundDark,
    surface = SurfaceDark,
    error = ErrorColor,
    onPrimary = OnPrimaryDark,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E6E6),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E6E6),
    onError = androidx.compose.ui.graphics.Color.White
)

/**
 * Tema principal de la aplicación.
 * Selecciona dinámicamente la paleta clara u oscura según [isSystemInDarkTheme],
 * y propaga tipografía y shapes definidos en este módulo.
 *
 * Para migrar a Material3 sería necesario reemplazar `MaterialTheme` por la versión de `material3`
 * y ajustar colores a un esquema de tonalidades dinámicas.
 */
@Composable
fun ReporteMaltratoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
