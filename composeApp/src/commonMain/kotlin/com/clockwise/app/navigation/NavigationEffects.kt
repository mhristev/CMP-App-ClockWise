package com.clockwise.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Navigation Effects - Additional styling components for modern UI effects
 * These complement the main navigation components with visual enhancements
 */

/**
 * Creates a modern glass-morphism backdrop effect for navigation elements
 * This can be used behind navigation bars or drawers for enhanced visual appeal
 */
@Composable
fun GlassMorphismBackdrop(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    blurRadius: androidx.compose.ui.unit.Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .blur(blurRadius)
    )
}

/**
 * Creates a subtle gradient overlay for better text contrast on transparent backgrounds
 */
@Composable
fun NavigationGradientOverlay(
    modifier: Modifier = Modifier,
    fromTop: Boolean = true
) {
    val primaryColor = MaterialTheme.colors.primary
    val gradient = if (fromTop) {
        Brush.verticalGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.1f),
                Color.Transparent,
                Color.Transparent
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                Color.Transparent,
                primaryColor.copy(alpha = 0.08f)
            )
        )
    }
    
    Box(
        modifier = modifier.background(brush = gradient)
    )
}

/**
 * Animated selection indicator that can be used for highlighting selected navigation items
 */
@Composable
fun SelectionIndicator(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .size(width = 24.dp, height = 3.dp)
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = RoundedCornerShape(1.5.dp)
                )
        )
    }
}