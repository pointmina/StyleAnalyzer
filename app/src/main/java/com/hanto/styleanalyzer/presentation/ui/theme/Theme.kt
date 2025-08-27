package com.hanto.styleanalyzer.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    // Primary - 블랙 계열
    primary = MinimalColors.Black,
    onPrimary = MinimalColors.White,
    primaryContainer = MinimalColors.DarkGray,
    onPrimaryContainer = MinimalColors.White,

    // Secondary - 그레이 계열
    secondary = MinimalColors.MediumGray,
    onSecondary = MinimalColors.White,
    secondaryContainer = MinimalColors.SoftGray,
    onSecondaryContainer = MinimalColors.Black,

    // Tertiary - 액센트 블루
    tertiary = MinimalColors.AccentBlue,
    onTertiary = MinimalColors.White,
    tertiaryContainer = MinimalColors.AccentBlue.copy(alpha = 0.1f),
    onTertiaryContainer = MinimalColors.AccentBlue,

    // Error
    error = MinimalColors.AccentRed,
    onError = MinimalColors.White,
    errorContainer = MinimalColors.AccentRed.copy(alpha = 0.1f),
    onErrorContainer = MinimalColors.AccentRed,

    // Background & Surface
    background = MinimalColors.BackgroundLight,
    onBackground = MinimalColors.TextPrimaryLight,
    surface = MinimalColors.SurfaceLight,
    onSurface = MinimalColors.TextPrimaryLight,
    surfaceVariant = MinimalColors.PaleGray,
    onSurfaceVariant = MinimalColors.TextSecondaryLight,

    // Outline & Borders
    outline = MinimalColors.BorderLight,
    outlineVariant = MinimalColors.SoftGray,

    // Inverse colors
    inverseSurface = MinimalColors.Black,
    inverseOnSurface = MinimalColors.White,
    inversePrimary = MinimalColors.White,

    // Surface tints
    surfaceTint = MinimalColors.Black,

    // Surface containers
    surfaceContainer = MinimalColors.White,
    surfaceContainerHigh = MinimalColors.PaleGray,
    surfaceContainerHighest = MinimalColors.SoftGray,
    surfaceContainerLow = MinimalColors.White,
    surfaceContainerLowest = MinimalColors.White
)

private val DarkColors = darkColorScheme(
    // Primary - 화이트 계열
    primary = MinimalColors.White,
    onPrimary = MinimalColors.Black,
    primaryContainer = MinimalColors.LightGray,
    onPrimaryContainer = MinimalColors.Black,

    // Secondary - 그레이 계열
    secondary = MinimalColors.LightGray,
    onSecondary = MinimalColors.Black,
    secondaryContainer = MinimalColors.DarkGray,
    onSecondaryContainer = MinimalColors.White,

    // Tertiary - 액센트 블루
    tertiary = MinimalColors.AccentBlue,
    onTertiary = MinimalColors.White,
    tertiaryContainer = MinimalColors.AccentBlue.copy(alpha = 0.2f),
    onTertiaryContainer = MinimalColors.AccentBlue,

    // Error
    error = MinimalColors.AccentRed,
    onError = MinimalColors.White,
    errorContainer = MinimalColors.AccentRed.copy(alpha = 0.2f),
    onErrorContainer = MinimalColors.AccentRed,

    // Background & Surface
    background = MinimalColors.BackgroundDark,
    onBackground = MinimalColors.TextPrimaryDark,
    surface = MinimalColors.SurfaceDark,
    onSurface = MinimalColors.TextPrimaryDark,
    surfaceVariant = MinimalColors.CharcoalBlack,
    onSurfaceVariant = MinimalColors.TextSecondaryDark,

    // Outline & Borders
    outline = MinimalColors.BorderDark,
    outlineVariant = MinimalColors.DarkGray,

    // Inverse colors
    inverseSurface = MinimalColors.White,
    inverseOnSurface = MinimalColors.Black,
    inversePrimary = MinimalColors.Black,

    // Surface tints
    surfaceTint = MinimalColors.White,

    // Surface containers
    surfaceContainer = MinimalColors.CharcoalBlack,
    surfaceContainerHigh = MinimalColors.DarkGray,
    surfaceContainerHighest = MinimalColors.MediumGray.copy(alpha = 0.3f),
    surfaceContainerLow = MinimalColors.Black,
    surfaceContainerLowest = MinimalColors.Black
)

/**
 * 미니멀 테마
 */
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