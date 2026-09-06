package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalAccentTheme = staticCompositionLocalOf { AccentTheme.M_MOTORSPORT }

@Composable
fun MyApplicationTheme(
    accentTheme: AccentTheme = AccentTheme.M_MOTORSPORT,
    customAccentColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val primary = customAccentColor ?: accentTheme.primaryColor
    val secondary = accentTheme.secondaryColor

    val colorScheme = darkColorScheme(
        primary = primary,
        onPrimary = Color.Black,
        primaryContainer = primary.copy(alpha = 0.2f),
        onPrimaryContainer = Color.White,
        secondary = secondary,
        onSecondary = Color.White,
        secondaryContainer = secondary.copy(alpha = 0.25f),
        onSecondaryContainer = Color.White,
        tertiary = MRed,
        onTertiary = Color.White,
        background = OledBlack,
        onBackground = Color.White,
        surface = DarkSurface,
        onSurface = Color.White,
        surfaceVariant = DarkElevated,
        onSurfaceVariant = Color(0xFFB0B7C3),
        outline = GlassBorder
    )

    CompositionLocalProvider(LocalAccentTheme provides accentTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
