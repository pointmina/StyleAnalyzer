package com.hanto.styleanalyzer.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// StyleAnalyzer 컬러 팔레트
private val StylePrimary = Color(0xFF6200EE)
private val StyleSecondary = Color(0xFF03DAC5)
private val StyleBackground = Color(0xFFFFFBFE)
private val StyleSurface = Color(0xFFFFFBFE)
private val StyleError = Color(0xFFB00020)

private val StylePrimaryDark = Color(0xFF3700B3)
private val StyleSecondaryDark = Color(0xFF018786)
private val StyleBackgroundDark = Color(0xFF121212)
private val StyleSurfaceDark = Color(0xFF121212)

private val LightColors = lightColorScheme(
    primary = StylePrimary,
    secondary = StyleSecondary,
    background = StyleBackground,
    surface = StyleSurface,
    error = StyleError,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = StylePrimaryDark,
    secondary = StyleSecondaryDark,
    background = StyleBackgroundDark,
    surface = StyleSurfaceDark,
    error = StyleError,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

@Composable
fun StyleAnalyzerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}