package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = StudioDarkBackground,
    primaryContainer = StudioCardSurfaceVariant,
    onPrimaryContainer = ElectricCyan,
    secondary = NeonPurple,
    onSecondary = StudioDarkBackground,
    secondaryContainer = StudioCardSurfaceVariant,
    onSecondaryContainer = NeonPurple,
    tertiary = EmeraldGreen,
    onTertiary = StudioDarkBackground,
    background = StudioDarkBackground,
    onBackground = TextPrimary,
    surface = StudioCardSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioCardSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StudioCardBorder,
    error = DangerRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force sleek audio dark theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = StudioDarkBackground.toArgb()
                window.navigationBarColor = StudioDarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
