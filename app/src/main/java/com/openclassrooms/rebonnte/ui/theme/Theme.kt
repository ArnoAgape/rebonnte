package com.openclassrooms.rebonnte.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Orange80,
    onPrimary = Color.Black,

    primaryContainer = Orange40,
    onPrimaryContainer = Color.White,

    secondary = BrownGrey80,
    onSecondary = Color.Black,

    tertiary = Red80,
    onTertiary = Color.Black,

    background = Color(0xFF121212),
    onBackground = Color(0xFFECECEC),

    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFECECEC),

    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCFCFCF),

    outline = Color(0xFF4A4A4A),
    outlineVariant = Color(0xFF2F2F2F)
)

private val LightColorScheme = lightColorScheme(
    primary = Orange40,
    onPrimary = Color.White,

    primaryContainer = Orange80,
    onPrimaryContainer = Color.White,

    secondary = BrownGrey40,
    onSecondary = Color.White,

    tertiary = Red40,
    onTertiary = Color.White,

    background = Color(0xFFFFFBF9),
    onBackground = Color(0xFF1C1B1A),

    surface = Color(0xFFFFFBF9),
    onSurface = Color(0xFF1C1B1A),

    surfaceVariant = Color(0xFFF4EAE4),
    onSurfaceVariant = Color(0xFF4A3A32),

    outline = Color(0xFFB9A59B),
    outlineVariant = Color(0xFFE0D2C8)
)

@Composable
fun RebonnteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}