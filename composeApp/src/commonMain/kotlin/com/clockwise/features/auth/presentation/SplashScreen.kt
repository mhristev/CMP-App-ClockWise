package com.clockwise.features.auth.presentation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.math.cos
import kotlin.random.Random

private val DarkPurple = Color(0xFF2D1B4E)
private val LightPurple = Color(0xFF4A2B8C)
private val Orange = Color(0xFFFF6B35)
private val Black = Color(0xFF121212)
private val White = Color(0xFFFFFFFF)

@Composable
fun SplashScreen(
    isRefreshingToken: Boolean = false,
    onSplashComplete: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    var showProgressIndicator by remember { mutableStateOf(false) }
    var currentLoadingMessage by remember { mutableStateOf("Loading...") }
    
    // Delightful loading messages that rotate
    val loadingMessages = listOf(
        "Winding up the clock...",
        "Syncing your schedule...",
        "Gathering your shifts...",
        "Preparing your workspace...",
        "Almost ready to go!"
    )
    
    // Logo animations with more personality
    val logoScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val logoAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(800)
    )
    
    // Breathing animation for the logo
    val infiniteTransition = rememberInfiniteTransition()
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Clock hand rotation
    val clockRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    // Progress indicator animation
    val progressAlpha by animateFloatAsState(
        targetValue = if (showProgressIndicator) 1f else 0f,
        animationSpec = tween(500)
    )
    
    // Floating particles animation
    val particles = remember { 
        List(8) { index ->
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 8f + 4f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                phase = Random.nextFloat() * 2f * kotlin.math.PI.toFloat()
            )
        }
    }
    
    val animationTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        // Show all content immediately
        showContent = true
        
        // Show progress after a shorter delay for better perceived performance
        delay(300)
        showProgressIndicator = true
        
        // Cycle through loading messages for entertainment
        if (!isRefreshingToken) {
            loadingMessages.forEachIndexed { index, message ->
                currentLoadingMessage = message
                delay(400) // Faster message rotation
            }
            delay(200) // Brief pause at the end
            onSplashComplete()
        }
    }
    
    // Complete splash when token refresh is done
    LaunchedEffect(isRefreshingToken) {
        if (!isRefreshingToken && showProgressIndicator) {
            // Faster transition for returning users with welcoming message
            currentLoadingMessage = "Welcome back!"
            delay(500)
            onSplashComplete()
        }
    }
    
    // Cycle loading messages during token refresh
    LaunchedEffect(isRefreshingToken, showProgressIndicator) {
        if (isRefreshingToken && showProgressIndicator) {
            while (isRefreshingToken) {
                loadingMessages.forEach { message ->
                    if (isRefreshingToken) {
                        currentLoadingMessage = message
                        delay(1000)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Black,
                        Color(0xFF1A0D2E),
                        Black
                    ),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating particles background
        if (showContent) {
            particles.forEach { particle ->
                FloatingParticle(
                    particle = particle,
                    animationTime = animationTime,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Enhanced Logo/Brand section with breathing and gradient
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale * breathingScale)
                    .alpha(logoAlpha)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                LightPurple,
                                DarkPurple,
                                LightPurple.copy(alpha = 0.8f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Rotating clock emoji with subtle animation
                Text(
                    text = "⏰",
                    fontSize = 56.sp,
                    color = White,
                    modifier = Modifier.rotate(clockRotation * 0.1f) // Subtle rotation
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // App name - static, appears with logo
            if (showContent) {
                Text(
                    text = "ClockWise",
                    style = MaterialTheme.typography.h3.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp,
                        fontFamily = FontFamily.Cursive // Comic-style font
                    ),
                    color = White
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tagline - static, appears with logo
            if (showContent) {
                Text(
                    text = "Smart Workforce Management",
                    style = MaterialTheme.typography.subtitle1.copy(
                        fontSize = 16.sp
                    ),
                    color = White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(56.dp))
            
            // Enhanced loading indicator with personality
            AnimatedVisibility(
                visible = showProgressIndicator,
                enter = fadeIn(tween(600)) + slideInVertically { it / 2 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Custom clock-themed loading indicator
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer ring
                        CircularProgressIndicator(
                            color = Orange.copy(alpha = 0.3f),
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .size(48.dp)
                                .alpha(progressAlpha)
                        )
                        // Inner spinning circle
                        CircularProgressIndicator(
                            color = Orange,
                            strokeWidth = 3.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .alpha(progressAlpha)
                        )
                        // Center clock emoji
                        Text(
                            text = "⏱️",
                            fontSize = 16.sp,
                            modifier = Modifier.rotate(clockRotation * 0.5f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Animated loading text with personality
                    AnimatedContent(
                        targetState = currentLoadingMessage,
                        transitionSpec = {
                            fadeIn(tween(300)) + slideInVertically { it / 3 } togetherWith
                            fadeOut(tween(200)) + slideOutVertically { -it / 3 }
                        }
                    ) { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.body2.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = White.copy(alpha = 0.9f),
                            modifier = Modifier.alpha(progressAlpha)
                        )
                    }
                }
            }
        }
        
        // Version info at bottom - static, appears with logo
        if (showContent) {
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.caption.copy(
                    fontSize = 12.sp
                ),
                color = White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }
    }
}

// Data class for floating particles
data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val phase: Float
)

// Floating particle component for magical background effect
@Composable
fun FloatingParticle(
    particle: Particle,
    animationTime: Float,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val screenWidth = maxWidth.value
        val screenHeight = maxHeight.value
        
        // Calculate particle position with floating motion
        val currentX = (particle.x * screenWidth + sin(animationTime * 2 * kotlin.math.PI + particle.phase) * 30).toFloat()
        val currentY = (particle.y * screenHeight + cos(animationTime * 1.5 * kotlin.math.PI + particle.phase) * 20).toFloat()
        
        // Opacity animation
        val alpha = (sin(animationTime * 3 * kotlin.math.PI + particle.phase) * 0.3 + 0.4).toFloat()
        
        Box(
            modifier = Modifier
                .offset(
                    x = currentX.dp.coerceIn(0.dp, maxWidth - particle.size.dp),
                    y = currentY.dp.coerceIn(0.dp, maxHeight - particle.size.dp)
                )
                .size(particle.size.dp)
                .alpha(alpha.coerceIn(0.1f, 0.7f))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4A2B8C).copy(alpha = 0.6f),
                            Color(0xFFFF6B35).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
} 