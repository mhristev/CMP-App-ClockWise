package com.clockwise.features.availability.presentation.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Month

/**
 * Header component for the calendar with month navigation
 */
fun getMonthName(month: Month): String {
    return when (month) {
        Month.JANUARY -> "January"
        Month.FEBRUARY -> "February"
        Month.MARCH -> "March"
        Month.APRIL -> "April"
        Month.MAY -> "May"
        Month.JUNE -> "June"
        Month.JULY -> "July"
        Month.AUGUST -> "August"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "October"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "December"
        else -> "Unknown"
    }
}
@Composable
fun CalendarHeader(
    currentMonth: Month,
    currentYear: Int,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonthClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous Month"
            )
        }
        
        Text(
            text = "${getMonthName(currentMonth)} $currentYear",
            style = MaterialTheme.typography.h6.copy(
                fontWeight = FontWeight.Bold
            )
        )
        
        IconButton(onClick = onNextMonthClick) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Month"
            )
        }
    }
} 