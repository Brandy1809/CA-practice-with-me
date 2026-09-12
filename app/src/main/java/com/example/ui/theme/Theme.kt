package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VintageLightColorScheme = lightColorScheme(
    primary = VintageNavy,
    onPrimary = Color.White,
    primaryContainer = VintagePaperSheet,
    onPrimaryContainer = VintageNavyDeep,
    secondary = VintageGreen,
    onSecondary = Color.White,
    secondaryContainer = VintageGreenBg,
    onSecondaryContainer = VintageGreen,
    tertiary = VintageGold,
    onTertiary = Color.White,
    background = VintagePaperBg,
    onBackground = VintageInk,
    surface = VintagePaperSheet,
    onSurface = VintageInk,
    surfaceVariant = VintagePaperBg,
    onSurfaceVariant = VintageInkSoft,
    outline = VintageLine,
    outlineVariant = VintageLine.copy(alpha = 0.5f),
    error = VintageRed,
    errorContainer = VintageRedBg,
    onError = Color.White,
    onErrorContainer = VintageRed
)

private val VintageDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7EA7D6),
    onPrimary = Color(0xFF132840),
    primaryContainer = Color(0xFF1D3350),
    onPrimaryContainer = Color(0xFFD6E4F0),
    secondary = Color(0xFF88BD90),
    onSecondary = Color(0xFF0F3816),
    secondaryContainer = Color(0xFF28482D),
    onSecondaryContainer = Color(0xFFD4EAD6),
    tertiary = Color(0xFFE2C482),
    onTertiary = Color(0xFF3F3108),
    background = Color(0xFF1E1D1A),
    onBackground = Color(0xFFEAE5DA),
    surface = Color(0xFF272621),
    onSurface = Color(0xFFEAE5DA),
    surfaceVariant = Color(0xFF34332D),
    onSurfaceVariant = Color(0xFFB5B0A2),
    outline = Color(0xFF5A574E),
    outlineVariant = Color(0xFF423F38),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VintageDarkColorScheme else VintageLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
