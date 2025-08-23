package com.clockwise.shared.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * A delightful, bouncy FloatingActionButton with breathing animation and press feedback
 */
@Composable
fun DelightfulMenuButton(
    onClick: () -> Unit,
    icon: ImageVector,
    isDrawerOpen: Boolean = false,
    modifier: Modifier = Modifier,
    contentDescription: String = "Menu"
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    
    // Breathing animation when idle
    val infiniteTransition = rememberInfiniteTransition()
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Press animation
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else breathingScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        )
    )
    
    // Rotation when drawer opens/closes
    val rotation by animateFloatAsState(
        targetValue = if (isDrawerOpen) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    // Elevation animation
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2.dp.value else 8.dp.value,
        animationSpec = spring(stiffness = Spring.StiffnessHigh)
    )
    
    FloatingActionButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = modifier
            .scale(pressScale)
            .rotate(rotation)
            .semantics {
                this.contentDescription = contentDescription
                role = Role.Button
            },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = MaterialTheme.colors.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(elevation.dp),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Already set on parent
            modifier = Modifier.size(20.dp)
        )
    }
    
    // Reset press state
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(100)
            isPressed = false
        }
    }
}

/**
 * Floating particles for background decoration
 */
@Composable
fun FloatingParticles(
    count: Int = 8,
    colors: List<Color> = listOf(
        Color(0xFF4A2B8C),
        Color(0xFFFF6B35),
        Color.White.copy(alpha = 0.6f)
    ),
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    
    data class Particle(
        val x: Float,
        val y: Float,
        val size: Dp,
        val color: Color,
        val speed: Float,
        val animationDelay: Int
    )
    
    val particles = remember {
        (0 until count).map {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = (Random.nextFloat() * 6 + 2).dp,
                color = colors.random(),
                speed = Random.nextFloat() * 0.5f + 0.2f,
                animationDelay = Random.nextInt(0, 2000)
            )
        }
    }
    
    Box(modifier = modifier) {
        particles.forEach { particle ->
            val infiniteTransition = rememberInfiniteTransition()
            
            val yOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween((3000 / particle.speed).toInt(), easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 0.8f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopStart)
                    .offset(
                        x = (particle.x * with(density) { 300.dp.toPx() }).dp / density.density,
                        y = (particle.y * with(density) { 500.dp.toPx() }).dp / density.density + yOffset.dp
                    )
                    .size(particle.size)
                    .alpha(alpha)
                    .background(
                        color = particle.color,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Success celebration with confetti particles
 */
@Composable
fun SuccessCelebration(
    isVisible: Boolean,
    onComplete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    
    val confettiColors = listOf(
        Color(0xFF4A2B8C), // Purple
        Color(0xFFFF6B35), // Orange
        Color(0xFF4CAF50), // Green
        Color(0xFFF44336), // Red
        Color(0xFFFFEB3B), // Yellow
        Color(0xFF9C27B0)  // Light Purple
    )
    
    data class ConfettiPiece(
        val color: Color,
        val startX: Float,
        val startY: Float,
        val velocityX: Float,
        val velocityY: Float,
        val rotation: Float,
        val size: Dp
    )
    
    val confetti = remember {
        (0..30).map {
            ConfettiPiece(
                color = confettiColors.random(),
                startX = Random.nextFloat() * 400f,
                startY = -50f,
                velocityX = (Random.nextFloat() - 0.5f) * 200f,
                velocityY = Random.nextFloat() * 300f + 100f,
                rotation = Random.nextFloat() * 360f,
                size = (Random.nextFloat() * 8 + 4).dp
            )
        }
    }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(4000)
            onComplete()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        confetti.forEach { piece ->
            val infiniteTransition = rememberInfiniteTransition()
            
            val yPosition by infiniteTransition.animateFloat(
                initialValue = piece.startY,
                targetValue = piece.startY + 600f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = EaseIn),
                    repeatMode = RepeatMode.Restart
                )
            )
            
            val xPosition by infiniteTransition.animateFloat(
                initialValue = piece.startX,
                targetValue = piece.startX + piece.velocityX,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = piece.rotation,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            
            val alpha by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = EaseOut),
                    repeatMode = RepeatMode.Restart
                )
            )
            
            Box(
                modifier = Modifier
                    .offset(x = xPosition.dp, y = yPosition.dp)
                    .rotate(rotation)
                    .size(piece.size)
                    .alpha(alpha)
                    .background(
                        color = piece.color,
                        shape = if (Random.nextBoolean()) CircleShape else RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

/**
 * Enhanced loading indicator with clock theme
 */
@Composable
fun DelightfulLoadingIndicator(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(scale),
            contentAlignment = Alignment.Center
        ) {
            // Outer ring
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.3f),
                strokeWidth = 3.dp
            )
            
            // Inner ring
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 2.dp
            )
            
            // Center clock emoji with rotation
            Text(
                text = "⏰",
                fontSize = 20.sp,
                modifier = Modifier.rotate(rotation)
            )
        }
        
        Text(
            text = message,
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )
    }
}

/**
 * Encouraging empty state component
 */
@Composable
fun EncouragingEmptyState(
    emoji: String = "📅",
    title: String = "All clear!",
    subtitle: String = "Nothing scheduled right now",
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 64.sp,
            modifier = Modifier
                .offset(y = floatingOffset.dp)
                .rotate(rotation)
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colors.primary
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                    contentColor = MaterialTheme.colors.primary
                )
            ) {
                Text(text = actionText)
            }
        }
        
        Text(
            text = "✨ Every great day starts with a clear schedule!",
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
        )
    }
}