package com.clockwise.app.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * ClockWise Shape System
 * Modern rounded corner system with semantic naming
 */

val ClockWiseShapes = Shapes(
    small = RoundedCornerShape(8.dp),    // Small components like chips, badges
    medium = RoundedCornerShape(12.dp),  // Cards, buttons
    large = RoundedCornerShape(16.dp)    // Dialogs, bottom sheets
)

// ==================== EXTENDED SHAPE SYSTEM ====================

object ClockWiseShapeTokens {
    // No corner radius
    val None = RoundedCornerShape(0.dp)
    
    // Extra small radius for subtle roundness
    val ExtraSmall = RoundedCornerShape(4.dp)
    
    // Small radius for chips, badges, small buttons
    val Small = RoundedCornerShape(8.dp)
    
    // Medium radius for cards, input fields, regular buttons
    val Medium = RoundedCornerShape(12.dp)
    
    // Large radius for dialogs, sheets, large cards
    val Large = RoundedCornerShape(16.dp)
    
    // Extra large for special components
    val ExtraLarge = RoundedCornerShape(24.dp)
    
    // Full rounded (circular)
    val Full = RoundedCornerShape(50)
}

// ==================== COMPONENT-SPECIFIC SHAPES ====================

object ClockWiseComponentShapes {
    // Button shapes
    val ButtonSmall = ClockWiseShapeTokens.Small
    val ButtonMedium = ClockWiseShapeTokens.Medium
    val ButtonLarge = ClockWiseShapeTokens.Large
    
    // Card shapes
    val Card = ClockWiseShapeTokens.Medium
    val CardLarge = ClockWiseShapeTokens.Large
    val CardRounded = ClockWiseShapeTokens.ExtraLarge
    
    // Input field shapes
    val TextField = ClockWiseShapeTokens.Medium
    val TextFieldSmall = ClockWiseShapeTokens.Small
    
    // Chip and badge shapes
    val Chip = ClockWiseShapeTokens.Small
    val Badge = ClockWiseShapeTokens.Full
    
    // Dialog and modal shapes
    val Dialog = ClockWiseShapeTokens.Large
    val BottomSheet = RoundedCornerShape(
        topStart = 24.dp,
        topEnd = 24.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Navigation shapes
    val BottomNav = ClockWiseShapeTokens.None
    val TopBar = ClockWiseShapeTokens.None
    val Tab = ClockWiseShapeTokens.Small
    
    // Container shapes
    val Container = ClockWiseShapeTokens.Medium
    val ContainerLarge = ClockWiseShapeTokens.Large
    
    // Image shapes
    val ImageSmall = ClockWiseShapeTokens.Small
    val ImageMedium = ClockWiseShapeTokens.Medium
    val ImageLarge = ClockWiseShapeTokens.Large
    val ImageCircular = ClockWiseShapeTokens.Full
    
    // Progress indicator shapes
    val ProgressLinear = ClockWiseShapeTokens.Full
    val ProgressCircular = ClockWiseShapeTokens.Full
    
    // Divider shapes (for custom dividers with rounded ends)
    val Divider = RoundedCornerShape(1.dp)
}