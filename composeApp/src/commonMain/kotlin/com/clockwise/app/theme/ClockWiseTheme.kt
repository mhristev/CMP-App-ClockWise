package com.clockwise.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * ClockWise Theme System
 * Professional Material Design 3 inspired theme with custom color palette
 */

// ==================== LOCAL PROVIDERS ====================

private val LocalClockWiseColors = staticCompositionLocalOf<ClockWiseColorScheme> {
    error("No ClockWiseColorScheme provided")
}

// ==================== THEME ACCESS ====================

object ClockWiseTheme {
    val colors: ClockWiseColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalClockWiseColors.current
}

// ==================== MATERIAL THEME CONVERSION ====================

private fun ClockWiseColorScheme.toMaterialLightColors() = lightColors(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
)

private fun ClockWiseColorScheme.toMaterialDarkColors() = darkColors(
    primary = primary,
    primaryVariant = primaryVariant,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    background = background,
    surface = surface,
    error = error,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    onBackground = onBackground,
    onSurface = onSurface,
    onError = onError
)

// ==================== THEME COMPOSABLE ====================

@Composable
fun ClockWiseThemeProvider(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: ClockWiseColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    content: @Composable () -> Unit
) {
    val materialColors = if (darkTheme) {
        colors.toMaterialDarkColors()
    } else {
        colors.toMaterialLightColors()
    }

    CompositionLocalProvider(
        LocalClockWiseColors provides colors
    ) {
        MaterialTheme(
            colors = materialColors,
            typography = ClockWiseTypography,
            shapes = ClockWiseShapes,
            content = content
        )
    }
}

// ==================== THEME VARIANTS ====================

@Composable
fun ShiftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val shiftColors = if (darkTheme) {
        DarkColorScheme.copy(
            primary = ShiftColors.DarkPrimary,
            secondary = ShiftColors.DarkSecondary,
            background = ShiftColors.DarkBackground,
            accent = ShiftColors.DarkAccent
        )
    } else {
        LightColorScheme.copy(
            primary = ShiftColors.LightPrimary,
            secondary = ShiftColors.LightSecondary,
            background = ShiftColors.LightBackground,
            accent = ShiftColors.LightAccent
        )
    }
    
    ClockWiseThemeProvider(
        darkTheme = darkTheme,
        colors = shiftColors,
        content = content
    )
}

@Composable
fun TimeTrackingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val timeTrackingColors = if (darkTheme) {
        DarkColorScheme.copy(
            primary = TimeTrackingColors.DarkPrimary,
            secondary = TimeTrackingColors.DarkSecondary,
            background = TimeTrackingColors.DarkBackground,
            accent = TimeTrackingColors.DarkAccent
        )
    } else {
        LightColorScheme.copy(
            primary = TimeTrackingColors.LightPrimary,
            secondary = TimeTrackingColors.LightSecondary,
            background = TimeTrackingColors.LightBackground,
            accent = TimeTrackingColors.LightAccent
        )
    }
    
    ClockWiseThemeProvider(
        darkTheme = darkTheme,
        colors = timeTrackingColors,
        content = content
    )
}

@Composable
fun ProfileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val profileColors = if (darkTheme) {
        DarkColorScheme.copy(
            primary = ProfileColors.DarkPrimary,
            secondary = ProfileColors.DarkSecondary,
            background = ProfileColors.DarkBackground,
            accent = ProfileColors.DarkAccent
        )
    } else {
        LightColorScheme.copy(
            primary = ProfileColors.LightPrimary,
            secondary = ProfileColors.LightSecondary,
            background = ProfileColors.LightBackground,
            accent = ProfileColors.LightAccent
        )
    }
    
    ClockWiseThemeProvider(
        darkTheme = darkTheme,
        colors = profileColors,
        content = content
    )
}

@Composable
fun BusinessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val businessColors = if (darkTheme) {
        DarkColorScheme.copy(
            primary = BusinessColors.DarkPrimary,
            secondary = BusinessColors.DarkSecondary,
            background = BusinessColors.DarkBackground,
            accent = BusinessColors.DarkAccent
        )
    } else {
        LightColorScheme.copy(
            primary = BusinessColors.LightPrimary,
            secondary = BusinessColors.LightSecondary,
            background = BusinessColors.LightBackground,
            accent = BusinessColors.LightAccent
        )
    }
    
    ClockWiseThemeProvider(
        darkTheme = darkTheme,
        colors = businessColors,
        content = content
    )
}