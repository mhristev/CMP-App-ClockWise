package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clockwise.features.shift.presentation.theme.ShiftColors
import kotlinx.coroutines.delay

@Composable
fun LoadingIndicator(
    message: String = "Loading your schedule...",
    showFunMessages: Boolean = true
) {
    var currentMessage by remember { mutableStateOf(message) }
    
    // Fun loading messages for schedule loading
    val funMessages = listOf(
        "Organizing your shifts...",
        "Checking your calendar...",
        "Syncing with the team...",
        "Almost there!",
        message
    )
    
    // Rotating animation for clock emoji
    val infiniteTransition = rememberInfiniteTransition()
    val clockRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    // Pulsing animation for the container
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Cycle through fun messages
    LaunchedEffect(showFunMessages) {
        if (showFunMessages) {
            funMessages.forEach { msg ->
                currentMessage = msg
                delay(1500)
            }
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Enhanced loading indicator with personality
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring with gradient
                CircularProgressIndicator(
                    color = ShiftColors.Primary.copy(alpha = 0.3f),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(80.dp)
                )
                
                // Inner spinning ring
                CircularProgressIndicator(
                    color = ShiftColors.Primary,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(60.dp)
                )
                
                // Center clock emoji
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    ShiftColors.Primary.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⏰",
                        fontSize = 20.sp,
                        modifier = Modifier.rotate(clockRotation * 0.1f)
                    )
                }
            }
            
            // Animated message
            if (showFunMessages) {
                Text(
                    text = currentMessage,
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = message,
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}