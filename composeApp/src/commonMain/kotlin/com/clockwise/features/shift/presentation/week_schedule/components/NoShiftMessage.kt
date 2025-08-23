package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clockwise.features.shift.presentation.theme.ShiftColors

@Composable
fun NoShiftsMessage(
    onRefresh: (() -> Unit)? = null
) {
    // Floating animation for the emoji
    val infiniteTransition = rememberInfiniteTransition()
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Gentle rotation for the calendar emoji
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Breathing animation for the container
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .scale(breathingScale)
                .padding(32.dp)
        ) {
            // Large emoji with floating animation
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .offset(y = floatingOffset.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ShiftColors.Primary.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            radius = 150f
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📅",
                    fontSize = 64.sp,
                    modifier = Modifier.rotate(rotation)
                )
            }
            
            // Encouraging headline
            Text(
                text = "Your schedule is clear!",
                style = MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )
            
            // Friendly sub-message
            Text(
                text = "Time to relax or pick up some extra shifts.✨",
                style = MaterialTheme.typography.body2.copy(
                    fontSize = 16.sp
                ),
                color = ShiftColors.TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Optional refresh button with personality
            onRefresh?.let { refreshAction ->
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = refreshAction,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    ShiftColors.Primary,
                                    ShiftColors.Primary.copy(alpha = 0.8f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "🔄 Check for updates",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Motivational footer
            Text(
                text = "Every great day starts with a clear schedule!",
                style = MaterialTheme.typography.caption.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                ),
                color = ShiftColors.TextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .alpha(0.8f)
            )
        }
    }
}
