package com.clockwise.app.theme

import androidx.compose.ui.graphics.Color

/**
 * ClockWise Professional Color System
 * Based on Purple-Pink-Yellow palette with light/dark mode support
 */

// ==================== BRAND COLORS ====================

object ClockWiseBrandColors {
    // Primary Brand Colors
    val PrimaryPurple = Color(0xFF46467A)          // Dark navy-purple
    val SecondaryPurple = Color(0xFF7768C6)        // Medium purple  
    val LightAccent = Color(0xFFE0DFFD)            // Light lavender
    val AccentYellow = Color(0xFFFFC212)           // Bright yellow
    val SecondaryPink = Color(0xFFF9B0C3)          // Soft pink
    
    // Extended Brand Palette
    val DeepPurple = Color(0xFF2D1B4E)             // Darkest purple
    val BrightPurple = Color(0xFF8C7AE6)           // Light purple
    val VibrantYellow = Color(0xFFFFD43B)          // Vibrant yellow
    val RosePink = Color(0xFFF78FB3)               // Rose pink
    val SoftLavender = Color(0xFFA29BFE)           // Soft lavender
}

// ==================== LIGHT MODE COLORS ====================

object ClockWiseLightColors {
    // Primary Colors
    val Primary = ClockWiseBrandColors.SecondaryPurple    // #7768C6
    val PrimaryVariant = ClockWiseBrandColors.PrimaryPurple // #46467A
    val Secondary = ClockWiseBrandColors.SecondaryPink     // #F9B0C3
    val SecondaryVariant = ClockWiseBrandColors.RosePink   // #F78FB3
    
    // Accent Colors
    val Accent = ClockWiseBrandColors.AccentYellow         // #FFC212
    val AccentVariant = ClockWiseBrandColors.VibrantYellow // #FFD43B
    
    // Surface & Background
    val Background = Color(0xFFF9FAFB)             // Light gray-blue
    val Surface = Color(0xFFFFFFFF)                // Pure white
    val SurfaceVariant = Color(0xFFF3F4F6)         // Light gray
    
    // Text Colors
    val OnPrimary = Color(0xFFFFFFFF)              // White on purple
    val OnSecondary = Color(0xFF111827)            // Dark on pink
    val OnAccent = Color(0xFF111827)               // Dark on yellow
    val OnBackground = Color(0xFF111827)           // Dark text
    val OnSurface = Color(0xFF111827)              // Dark text
    val OnSurfaceVariant = Color(0xFF6B7280)       // Gray text
    
    // Semantic Colors
    val Success = Color(0xFF22C55E)                // Green
    val Warning = Color(0xFFF59E0B)                // Orange
    val Error = Color(0xFFEF4444)                  // Red
    val Info = ClockWiseBrandColors.LightAccent    // Light purple
    
    // On Semantic Colors
    val OnSuccess = Color(0xFFFFFFFF)
    val OnWarning = Color(0xFF111827)
    val OnError = Color(0xFFFFFFFF)
    val OnInfo = Color(0xFF111827)
    
    // Borders & Dividers
    val Border = Color(0xFFE5E7EB)
    val Divider = Color(0xFFF3F4F6)
    
    // Shadows
    val Shadow = Color(0x1A000000)                 // 10% black
    val ShadowLight = Color(0x0D000000)            // 5% black
}

// ==================== DARK MODE COLORS ====================

object ClockWiseDarkColors {
    // Primary Colors
    val Primary = ClockWiseBrandColors.BrightPurple       // #8C7AE6
    val PrimaryVariant = ClockWiseBrandColors.SecondaryPurple // #7768C6
    val Secondary = ClockWiseBrandColors.RosePink         // #F78FB3
    val SecondaryVariant = ClockWiseBrandColors.SecondaryPink // #F9B0C3
    
    // Accent Colors
    val Accent = ClockWiseBrandColors.VibrantYellow       // #FFD43B
    val AccentVariant = ClockWiseBrandColors.AccentYellow // #FFC212
    
    // Surface & Background
    val Background = Color(0xFF111827)             // Dark navy
    val Surface = Color(0xFF1F2937)                // Dark gray
    val SurfaceVariant = Color(0xFF374151)         // Medium gray
    
    // Text Colors
    val OnPrimary = Color(0xFFFFFFFF)              // White on purple
    val OnSecondary = Color(0xFF111827)            // Dark on pink
    val OnAccent = Color(0xFF111827)               // Dark on yellow
    val OnBackground = Color(0xFFF9FAFB)           // Light text
    val OnSurface = Color(0xFFF9FAFB)              // Light text
    val OnSurfaceVariant = Color(0xFF9CA3AF)       // Gray text
    
    // Semantic Colors
    val Success = Color(0xFF4ADE80)                // Light green
    val Warning = Color(0xFFFBBF24)                // Light orange
    val Error = Color(0xFFF87171)                  // Light red
    val Info = ClockWiseBrandColors.SoftLavender   // Soft lavender
    
    // On Semantic Colors
    val OnSuccess = Color(0xFF111827)
    val OnWarning = Color(0xFF111827)
    val OnError = Color(0xFF111827)
    val OnInfo = Color(0xFF111827)
    
    // Borders & Dividers
    val Border = Color(0xFF4B5563)
    val Divider = Color(0xFF374151)
    
    // Shadows
    val Shadow = Color(0x33000000)                 // 20% black
    val ShadowLight = Color(0x1A000000)            // 10% black
}

// ==================== FEATURE-SPECIFIC COLORS ====================

object ShiftColors {
    // Light Mode
    val LightPrimary = ClockWiseLightColors.Primary
    val LightSecondary = Color(0xFF8B7CF6)         // Lighter purple
    val LightBackground = Color(0xFFF8FAFC)
    val LightAccent = ClockWiseBrandColors.LightAccent
    
    // Dark Mode
    val DarkPrimary = ClockWiseDarkColors.Primary
    val DarkSecondary = Color(0xFF6366F1)          // Darker purple
    val DarkBackground = Color(0xFF0F172A)
    val DarkAccent = ClockWiseBrandColors.SoftLavender
}

object TimeTrackingColors {
    // Light Mode
    val LightPrimary = ClockWiseLightColors.Accent  // Yellow theme
    val LightSecondary = Color(0xFFFEF3C7)          // Light yellow
    val LightBackground = Color(0xFFFFFBEB)
    val LightAccent = Color(0xFFF59E0B)
    
    // Dark Mode
    val DarkPrimary = ClockWiseDarkColors.Accent    // Bright yellow
    val DarkSecondary = Color(0xFF92400E)           // Dark yellow
    val DarkBackground = Color(0xFF1C1917)
    val DarkAccent = Color(0xFFFCD34D)
}

object ProfileColors {
    // Light Mode
    val LightPrimary = ClockWiseLightColors.Secondary // Pink theme
    val LightSecondary = Color(0xFFFCE7F3)            // Light pink
    val LightBackground = Color(0xFFFDF2F8)
    val LightAccent = Color(0xFFEC4899)
    
    // Dark Mode
    val DarkPrimary = ClockWiseDarkColors.Secondary   // Rose pink
    val DarkSecondary = Color(0xFF9D174D)             // Dark pink
    val DarkBackground = Color(0xFF1E1E2E)
    val DarkAccent = Color(0xFFF472B6)
}

object BusinessColors {
    // Light Mode
    val LightPrimary = ClockWiseBrandColors.PrimaryPurple // Dark purple theme
    val LightSecondary = Color(0xFF6366F1)                // Indigo
    val LightBackground = Color(0xFFF8FAFC)
    val LightAccent = Color(0xFF4F46E5)
    
    // Dark Mode
    val DarkPrimary = ClockWiseBrandColors.DeepPurple     // Very dark purple
    val DarkSecondary = Color(0xFF312E81)                 // Dark indigo
    val DarkBackground = Color(0xFF0C0A1E)
    val DarkAccent = Color(0xFF7C3AED)
}

// ==================== COLOR COLLECTIONS ====================

data class ClockWiseColorScheme(
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val secondaryVariant: Color,
    val accent: Color,
    val accentVariant: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onAccent: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val info: Color,
    val onSuccess: Color,
    val onWarning: Color,
    val onError: Color,
    val onInfo: Color,
    val border: Color,
    val divider: Color,
    val shadow: Color,
    val shadowLight: Color
)

val LightColorScheme = ClockWiseColorScheme(
    primary = ClockWiseLightColors.Primary,
    primaryVariant = ClockWiseLightColors.PrimaryVariant,
    secondary = ClockWiseLightColors.Secondary,
    secondaryVariant = ClockWiseLightColors.SecondaryVariant,
    accent = ClockWiseLightColors.Accent,
    accentVariant = ClockWiseLightColors.AccentVariant,
    background = ClockWiseLightColors.Background,
    surface = ClockWiseLightColors.Surface,
    surfaceVariant = ClockWiseLightColors.SurfaceVariant,
    onPrimary = ClockWiseLightColors.OnPrimary,
    onSecondary = ClockWiseLightColors.OnSecondary,
    onAccent = ClockWiseLightColors.OnAccent,
    onBackground = ClockWiseLightColors.OnBackground,
    onSurface = ClockWiseLightColors.OnSurface,
    onSurfaceVariant = ClockWiseLightColors.OnSurfaceVariant,
    success = ClockWiseLightColors.Success,
    warning = ClockWiseLightColors.Warning,
    error = ClockWiseLightColors.Error,
    info = ClockWiseLightColors.Info,
    onSuccess = ClockWiseLightColors.OnSuccess,
    onWarning = ClockWiseLightColors.OnWarning,
    onError = ClockWiseLightColors.OnError,
    onInfo = ClockWiseLightColors.OnInfo,
    border = ClockWiseLightColors.Border,
    divider = ClockWiseLightColors.Divider,
    shadow = ClockWiseLightColors.Shadow,
    shadowLight = ClockWiseLightColors.ShadowLight
)

val DarkColorScheme = ClockWiseColorScheme(
    primary = ClockWiseDarkColors.Primary,
    primaryVariant = ClockWiseDarkColors.PrimaryVariant,
    secondary = ClockWiseDarkColors.Secondary,
    secondaryVariant = ClockWiseDarkColors.SecondaryVariant,
    accent = ClockWiseDarkColors.Accent,
    accentVariant = ClockWiseDarkColors.AccentVariant,
    background = ClockWiseDarkColors.Background,
    surface = ClockWiseDarkColors.Surface,
    surfaceVariant = ClockWiseDarkColors.SurfaceVariant,
    onPrimary = ClockWiseDarkColors.OnPrimary,
    onSecondary = ClockWiseDarkColors.OnSecondary,
    onAccent = ClockWiseDarkColors.OnAccent,
    onBackground = ClockWiseDarkColors.OnBackground,
    onSurface = ClockWiseDarkColors.OnSurface,
    onSurfaceVariant = ClockWiseDarkColors.OnSurfaceVariant,
    success = ClockWiseDarkColors.Success,
    warning = ClockWiseDarkColors.Warning,
    error = ClockWiseDarkColors.Error,
    info = ClockWiseDarkColors.Info,
    onSuccess = ClockWiseDarkColors.OnSuccess,
    onWarning = ClockWiseDarkColors.OnWarning,
    onError = ClockWiseDarkColors.OnError,
    onInfo = ClockWiseDarkColors.OnInfo,
    border = ClockWiseDarkColors.Border,
    divider = ClockWiseDarkColors.Divider,
    shadow = ClockWiseDarkColors.Shadow,
    shadowLight = ClockWiseDarkColors.ShadowLight
)