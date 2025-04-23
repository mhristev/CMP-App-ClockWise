package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.clockwise.features.shift.presentation.theme.ShiftColors

@Composable
fun ScheduleHeader(
    onTodayClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Weekly Schedule",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = ShiftColors.Primary
        )

        Button(
            onClick = onTodayClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ShiftColors.Primary,
                contentColor = Color.White
            )
        ) {
            Text("Today")
        }
    }
}