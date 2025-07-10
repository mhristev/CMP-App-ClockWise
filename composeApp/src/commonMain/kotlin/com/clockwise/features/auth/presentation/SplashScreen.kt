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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

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
    
    // Logo animation
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
    
    // Progress indicator animation
    val progressAlpha by animateFloatAsState(
        targetValue = if (showProgressIndicator) 1f else 0f,
        animationSpec = tween(500)
    )

    LaunchedEffect(Unit) {
        // Show all content immediately
        showContent = true
        
        // Show progress after a short delay
        delay(500)
        showProgressIndicator = true
        
        // Minimum splash duration (for better UX)
        if (!isRefreshingToken) {
            delay(3000) // Hold splash for 3 seconds
            onSplashComplete()
        }
    }
    
    // Complete splash when token refresh is done
    LaunchedEffect(isRefreshingToken) {
        if (!isRefreshingToken && showProgressIndicator) {
            // Ensure minimum display time even with fast refresh
            delay(1000)
            onSplashComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo/Brand section
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
                    .background(
                        color = LightPurple,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⏰",
                    fontSize = 56.sp,
                    color = White
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
            
            // Loading indicator and text
            AnimatedVisibility(
                visible = showProgressIndicator,
                enter = fadeIn(tween(600))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = White,
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .size(36.dp)
                            .alpha(progressAlpha)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = if (isRefreshingToken) "Signing you in..." else "Loading...",
                        style = MaterialTheme.typography.body2.copy(
                            fontSize = 14.sp
                        ),
                        color = White.copy(alpha = 0.7f),
                        modifier = Modifier.alpha(progressAlpha)
                    )
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