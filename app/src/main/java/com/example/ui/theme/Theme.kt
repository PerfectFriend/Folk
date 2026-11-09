package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

val FolkloreDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFD700), // Gold
    onPrimary = Color(0xFF140727),
    primaryContainer = Color(0xFF2C194D),
    onPrimaryContainer = Color(0xFFFFD700),
    secondary = Color(0xFF00FFCC), // Cyan
    onSecondary = Color(0xFF140727),
    secondaryContainer = Color(0xFF003D55),
    onSecondaryContainer = Color(0xFF00FFCC),
    tertiary = Color(0xFFFF9E00),
    onTertiary = Color(0xFF140727),
    background = Color(0xFF05010B),
    onBackground = Color.White,
    surface = Color(0xFF0F061E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1035),
    onSurfaceVariant = Color.White.copy(alpha = 0.8f),
    surfaceContainer = Color(0xFF140727),
    surfaceContainerLow = Color(0xFF0F061E),
    surfaceContainerHigh = Color(0xFF1E1035),
    surfaceContainerHighest = Color(0xFF2C194D),
    outline = Color(0xFFFFD700).copy(alpha = 0.3f),
    outlineVariant = Color.White.copy(alpha = 0.15f),
    error = Color(0xFFFF5252),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FolkloreDarkColorScheme,
        typography = Typography,
        content = content
    )
}
