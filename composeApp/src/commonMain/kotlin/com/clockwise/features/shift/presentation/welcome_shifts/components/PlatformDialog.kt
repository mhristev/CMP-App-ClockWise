package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.runtime.Composable

/**
 * Platform-specific dialog implementation to handle iOS Dialog issues
 */
@Composable
expect fun PlatformDialog(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
)

/**
 * Platform-specific full-screen modal implementation
 */
@Composable
expect fun PlatformFullScreenModal(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
)