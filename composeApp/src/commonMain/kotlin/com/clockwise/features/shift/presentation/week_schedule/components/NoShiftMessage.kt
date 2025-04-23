package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.clockwise.features.shift.presentation.theme.ShiftColors

@Composable
fun NoShiftsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No shifts scheduled",
            style = MaterialTheme.typography.body1,
            color = ShiftColors.TextSecondary
        )
    }
}
