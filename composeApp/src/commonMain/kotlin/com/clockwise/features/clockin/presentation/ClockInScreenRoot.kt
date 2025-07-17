package com.clockwise.features.clockin.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Root composable for the clock-in screen (temporarily disabled).
 */
@Composable
fun ClockInScreenRoot(
    modifier: Modifier = Modifier
) {
    // Temporary placeholder - clockin feature is disabled
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Clock-in feature is temporarily disabled")
    }
}
