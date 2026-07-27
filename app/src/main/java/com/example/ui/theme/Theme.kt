package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DeepNavy,
    primaryContainer = Color(0xFF00381F),
    onPrimaryContainer = NeonGreen,
    secondary = NeonCyan,
    onSecondary = DeepNavy,
    secondaryContainer = Color(0xFF003344),
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    background = DeepNavy,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF252542),
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = DangerRed
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A859),
    onPrimary = Color.White,
    secondary = Color(0xFF0080A8),
    onSecondary = Color.White,
    background = Color(0xFFF4F6FB),
    surface = Color.White,
    onBackground = Color(0xFF101223),
    onSurface = Color(0xFF101223)
)

@Composable
fun CyberTechTheme(
    darkTheme: Boolean = true, // Default dark theme as specified in requirements
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    CyberTechTheme(darkTheme = darkTheme, content = content)
}
