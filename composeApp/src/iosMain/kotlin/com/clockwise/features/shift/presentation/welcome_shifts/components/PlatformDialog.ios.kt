package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * iOS implementation using Popup instead of Dialog to avoid iOS-specific issues
 * with Dialog + AnimatedVisibility combinations
 */
@Composable
actual fun PlatformDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isVisible) {
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            // Separate animation control inside Popup for iOS
            var showContent by remember { mutableStateOf(false) }
            
            LaunchedEffect(isVisible) {
                if (isVisible) {
                    kotlinx.coroutines.delay(50) // Small delay for smoother animation
                    showContent = true
                }
            }
            
            DisposableEffect(isVisible) {
                onDispose {
                    showContent = false
                }
            }
            
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                content()
            }
        }
    }
}

/**
 * iOS full-screen modal using Box overlay approach for maximum compatibility
 * This avoids Dialog entirely on iOS to prevent rendering issues
 */
@Composable
actual fun PlatformFullScreenModal(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            content()
        }
    }
}