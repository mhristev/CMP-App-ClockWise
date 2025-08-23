package com.clockwise.app.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ClockWise Effects System
 * Professional gradients, shadows, and visual effects
 */

// ==================== GRADIENT BRUSHES ====================

object ClockWiseGradients {
    
    // Primary gradients using brand colors
    val PrimaryVertical = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.SecondaryPurple,
            ClockWiseBrandColors.PrimaryPurple
        )
    )
    
    val PrimaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            ClockWiseBrandColors.SecondaryPurple,
            ClockWiseBrandColors.PrimaryPurple
        )
    )
    
    val PrimaryRadial = Brush.radialGradient(
        colors = listOf(
            ClockWiseBrandColors.SecondaryPurple,
            ClockWiseBrandColors.PrimaryPurple
        )
    )
    
    // Secondary gradients (pink theme)
    val SecondaryVertical = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.SecondaryPink,
            ClockWiseBrandColors.RosePink
        )
    )
    
    val SecondaryHorizontal = Brush.horizontalGradient(
        colors = listOf(
            ClockWiseBrandColors.SecondaryPink,
            ClockWiseBrandColors.RosePink
        )
    )
    
    // Accent gradients (yellow theme)
    val AccentVertical = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.AccentYellow,
            ClockWiseBrandColors.VibrantYellow
        )
    )
    
    val AccentHorizontal = Brush.horizontalGradient(
        colors = listOf(
            ClockWiseBrandColors.AccentYellow,
            ClockWiseBrandColors.VibrantYellow
        )
    )
    
    // Feature-specific gradients
    val ShiftGradient = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.BrightPurple,
            ClockWiseBrandColors.SecondaryPurple
        )
    )
    
    val TimeTrackingGradient = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.VibrantYellow,
            ClockWiseBrandColors.AccentYellow
        )
    )
    
    val ProfileGradient = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.RosePink,
            ClockWiseBrandColors.SecondaryPink
        )
    )
    
    val BusinessGradient = Brush.verticalGradient(
        colors = listOf(
            ClockWiseBrandColors.PrimaryPurple,
            ClockWiseBrandColors.DeepPurple
        )
    )
    
    // Subtle background gradients
    val LightBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFAFBFC),
            Color(0xFFF8FAFC)
        )
    )
    
    val DarkBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A),
            Color(0xFF111827)
        )
    )
    
    // Glass morphism effects
    val GlassLight = Brush.verticalGradient(
        colors = listOf(
            Color(0x20FFFFFF),
            Color(0x10FFFFFF)
        )
    )
    
    val GlassDark = Brush.verticalGradient(
        colors = listOf(
            Color(0x30FFFFFF),
            Color(0x10FFFFFF)
        )
    )
    
    // Success, warning, error gradients
    val Success = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4ADE80),
            Color(0xFF22C55E)
        )
    )
    
    val Warning = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFBBF24),
            Color(0xFFF59E0B)
        )
    )
    
    val Error = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF87171),
            Color(0xFFEF4444)
        )
    )
}

// ==================== SHADOW DEFINITIONS ====================

object ClockWiseShadows {
    // Light mode shadows
    val Light = Shadow(
        color = Color(0x1A000000),  // 10% black
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )
    
    val Medium = Shadow(
        color = Color(0x26000000),  // 15% black
        offset = Offset(0f, 4f),
        blurRadius = 8f
    )
    
    val Heavy = Shadow(
        color = Color(0x33000000),  // 20% black
        offset = Offset(0f, 8f),
        blurRadius = 16f
    )
    
    // Dark mode shadows (more prominent)
    val DarkLight = Shadow(
        color = Color(0x40000000),  // 25% black
        offset = Offset(0f, 2f),
        blurRadius = 4f
    )
    
    val DarkMedium = Shadow(
        color = Color(0x4D000000),  // 30% black
        offset = Offset(0f, 4f),
        blurRadius = 8f
    )
    
    val DarkHeavy = Shadow(
        color = Color(0x66000000),  // 40% black
        offset = Offset(0f, 8f),
        blurRadius = 16f
    )
}

// Helper data class for shadows (since it's not imported)
data class Shadow(
    val color: Color,
    val offset: Offset,
    val blurRadius: Float
)

data class Offset(
    val x: Float,
    val y: Float
)

// ==================== MODIFIER EXTENSIONS ====================

fun Modifier.gradientBackground(
    gradient: Brush,
    shape: RoundedCornerShape = ClockWiseComponentShapes.Card
) = this
    .clip(shape)
    .background(gradient)

fun Modifier.primaryGradient() = this.gradientBackground(ClockWiseGradients.PrimaryVertical)
fun Modifier.secondaryGradient() = this.gradientBackground(ClockWiseGradients.SecondaryVertical)
fun Modifier.accentGradient() = this.gradientBackground(ClockWiseGradients.AccentVertical)

fun Modifier.shiftGradient() = this.gradientBackground(ClockWiseGradients.ShiftGradient)
fun Modifier.timeTrackingGradient() = this.gradientBackground(ClockWiseGradients.TimeTrackingGradient)
fun Modifier.profileGradient() = this.gradientBackground(ClockWiseGradients.ProfileGradient)
fun Modifier.businessGradient() = this.gradientBackground(ClockWiseGradients.BusinessGradient)

fun Modifier.glassEffect(isDark: Boolean = false) = this.gradientBackground(
    gradient = if (isDark) ClockWiseGradients.GlassDark else ClockWiseGradients.GlassLight,
    shape = ClockWiseComponentShapes.Card
)

// ==================== GLASS MORPHISM COMPOSABLE ====================

@Composable
fun GlassMorphicBox(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    blur: Dp = 10.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(ClockWiseComponentShapes.Card)
            .background(
                if (isDark) ClockWiseGradients.GlassDark else ClockWiseGradients.GlassLight
            )
            .blur(blur)
    ) {
        content()
    }
}

// ==================== ELEVATION SYSTEM ====================

object ClockWiseElevation {
    val None = 0.dp
    val Small = 2.dp      // Slight elevation for cards
    val Medium = 4.dp     // Standard cards and buttons
    val Large = 8.dp      // Prominent elements like FABs
    val ExtraLarge = 16.dp // Dialogs and navigation drawers
}

// ==================== CUSTOM EFFECTS ====================

fun Modifier.cardShadow(elevation: Dp = ClockWiseElevation.Medium) = this
    .offset(y = elevation / 2)
    .blur(elevation)
    .background(
        color = Color.Black.copy(alpha = 0.1f),
        shape = ClockWiseComponentShapes.Card
    )

fun Modifier.buttonShadow() = this.cardShadow(ClockWiseElevation.Small)

fun Modifier.dialogShadow() = this.cardShadow(ClockWiseElevation.ExtraLarge)