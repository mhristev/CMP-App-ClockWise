package com.clockwise.features.shift.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.clockwise.app.theme.ClockWiseTheme
import com.clockwise.app.theme.ClockWiseBrandColors

/**
 * Shift feature colors using the ClockWise design system
 */
object ShiftColors {
    
    @Composable
    fun primary(): Color = ClockWiseTheme.colors.primary
    
    @Composable
    fun background(): Color = ClockWiseTheme.colors.background
    
    @Composable
    fun accentLight(): Color = ClockWiseBrandColors.LightAccent
    
    @Composable
    fun accentDark(): Color = ClockWiseBrandColors.PrimaryPurple
    
    @Composable
    fun textPrimary(): Color = ClockWiseTheme.colors.onBackground
    
    @Composable
    fun textSecondary(): Color = ClockWiseTheme.colors.onSurfaceVariant
    
    @Composable
    fun todayHighlight(): Color = ClockWiseBrandColors.LightAccent
    
    @Composable
    fun weekendHighlight(): Color = ClockWiseBrandColors.AccentYellow.copy(alpha = 0.3f)
    
    @Composable
    fun shiftCard(): Color = ClockWiseTheme.colors.surface
    
    // Legacy colors for backward compatibility
    @Deprecated("Use primary() instead")
    val Primary = Color(0xFF7768C6)
    
    @Deprecated("Use background() instead")
    val Background = Color(0xFFE0DFFD)
    
    @Deprecated("Use textPrimary() instead")
    val TextPrimary = Color(0xFF333333)
    
    @Deprecated("Use textSecondary() instead")
    val TextSecondary = Color(0xFF666666)
    
    @Deprecated("Use todayHighlight() instead")
    val TodayHighlight = Color(0xFFE6E0F3)
} 