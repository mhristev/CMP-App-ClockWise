package com.clockwise.features.shift.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.features.shift.core.presentation.theme.ShiftColors
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@Composable
fun DayButtonWithDate(
    day: DayOfWeek,
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(48.dp)
            .background(
                when {
                    isSelected -> ShiftColors.Primary
                    isToday -> ShiftColors.TodayHighlight
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.name.take(3),
            style = MaterialTheme.typography.subtitle2,
            color = if (isSelected) Color.White else ShiftColors.Primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.body2,
            color = if (isSelected) Color.White else ShiftColors.TextPrimary,
            textAlign = TextAlign.Center
        )
    }
} 