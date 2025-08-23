package com.clockwise.features.profile.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.clockwise.app.theme.ClockWiseTheme
import com.clockwise.app.theme.ClockWiseBrandColors

/**
 * Profile feature colors using the ClockWise design system
 */
object ProfileColors {
    
    @Composable
    fun primary(): Color = ClockWiseTheme.colors.secondary
    
    @Composable
    fun background(): Color = ClockWiseTheme.colors.background
    
    @Composable
    fun accentLight(): Color = ClockWiseBrandColors.SecondaryPink
    
    @Composable
    fun accentDark(): Color = ClockWiseBrandColors.RosePink
    
    @Composable
    fun textPrimary(): Color = ClockWiseTheme.colors.onBackground
    
    @Composable
    fun textSecondary(): Color = ClockWiseTheme.colors.onSurfaceVariant
    
    @Composable
    fun surface(): Color = ClockWiseTheme.colors.surface
    
    // Legacy colors for backward compatibility
    @Deprecated("Use primary() instead")
    val Primary = Color(0xFFF9B0C3)
    
    @Deprecated("Use background() instead")
    val Background = Color(0xFFFDF2F8)
    
    @Deprecated("Use textPrimary() instead")
    val TextPrimary = Color(0xFF333333)
    
    @Deprecated("Use textSecondary() instead")
    val TextSecondary = Color(0xFF666666)
    
    @Deprecated("Use surface() instead")
    val Surface = Color.White
} 