package com.clockwise.features.availability.presentation.calendar.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Display weekday names in row format
 */
@Composable
fun WeekdayHeader() {
    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        weekdays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                style = MaterialTheme.typography.body2.copy(
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }
    }
} 