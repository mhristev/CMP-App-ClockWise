package com.clockwise.app.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigateToBusinessUnit: () -> Unit,
    onNavigateToClockIn: () -> Unit,
    onNavigateToSchedule: () -> Unit
) {
    // Enhanced glass-morphism effect background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colors.surface.copy(alpha = 0.95f),
                        MaterialTheme.colors.surface
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                ),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                clip = false
            )
    ) {
        // Subtle top border for definition
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    MaterialTheme.colors.primary.copy(alpha = 0.1f)
                )
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Business Unit button
            ModernBottomNavItem(
                icon = Icons.Default.Business,
                label = "Business",
                isSelected = currentRoute == NavigationRoutes.BusinessUnitLanding.route,
                onClick = onNavigateToBusinessUnit
            )
            
            // Clock In button (special highlighting as primary action)
            ModernBottomNavItem(
                icon = Icons.Default.AccessTime,
                label = "Clock In",
                isSelected = currentRoute == NavigationRoutes.Welcome.route,
                onClick = onNavigateToClockIn,
                isPrimary = true
            )
            
            // Weekly Schedule button
            ModernBottomNavItem(
                icon = Icons.Default.CalendarToday,
                label = "Schedule",
                isSelected = currentRoute == NavigationRoutes.WeeklySchedule.route,
                onClick = onNavigateToSchedule
            )
            
        }
    }
}

@Composable
private fun ModernBottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isPrimary: Boolean = false
) {
    val primaryColor = MaterialTheme.colors.primary
    val onPrimaryColor = MaterialTheme.colors.onPrimary
    val onSurfaceColor = MaterialTheme.colors.onSurface
    
    // Animated scale for selection feedback
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200)
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(200)
    )
    
    // Enhanced selection colors and background
    val backgroundColor = when {
        isSelected && isPrimary -> Brush.linearGradient(
            colors = listOf(
                primaryColor,
                primaryColor.copy(alpha = 0.8f)
            )
        )
        isSelected -> Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.15f),
                primaryColor.copy(alpha = 0.08f)
            )
        )
        else -> Brush.radialGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }
    
    val contentColor = when {
        isSelected && isPrimary -> onPrimaryColor
        isSelected -> primaryColor
        else -> onSurfaceColor.copy(alpha = 0.7f)
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .background(
                brush = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Enhanced icon container
        Box(
            modifier = Modifier
                .size(if (isPrimary) 40.dp else 36.dp)
                .clip(if (isPrimary) RoundedCornerShape(12.dp) else CircleShape)
                .background(
                    when {
                        isSelected && !isPrimary -> primaryColor.copy(alpha = 0.2f)
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier
                    .size(if (isPrimary) 24.dp else 22.dp)
                    .scale(iconScale)
            )
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Enhanced label typography
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = if (isPrimary && isSelected) 12.sp else 11.sp
            ),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        // Enhanced selection indicator
        if (isSelected) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .width(if (isPrimary) 20.dp else 6.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (isPrimary) onPrimaryColor else primaryColor
                    )
            )
        }
    }
}