package com.clockwise.features.availability.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.availability.domain.model.Availability
import kotlinx.datetime.LocalDate

fun calculateDuration(startTime: String, endTime: String): String {
    try {
        // Parse the time strings
        val startComponents = startTime.split(":")
        val endComponents = endTime.split(":")

        if (startComponents.size < 2 || endComponents.size < 2) {
            return "Unknown"
        }

        val startHour = startComponents[0].toIntOrNull() ?: return "Unknown"
        val startMinute = startComponents[1].toIntOrNull() ?: return "Unknown"
        val endHour = endComponents[0].toIntOrNull() ?: return "Unknown"
        val endMinute = endComponents[1].toIntOrNull() ?: return "Unknown"

        // Calculate total minutes
        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute
        val durationMinutes = endTotalMinutes - startTotalMinutes

        if (durationMinutes <= 0) {
            return "Invalid duration"
        }

        // Convert to hours and minutes
        val hours = durationMinutes / 60
        val minutes = durationMinutes % 60

        // Format the result
        return when {
            hours > 0 && minutes > 0 -> "$hours hour${if (hours > 1) "s" else ""} $minutes minute${if (minutes > 1) "s" else ""}"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""}"
            else -> "$minutes minute${if (minutes > 1) "s" else ""}"
        }
    } catch (e: Exception) {
        return "Unknown"
    }
}
fun formatDate(date: LocalDate): String {
    val monthName = getMonthName(date.month)
    return "$monthName ${date.dayOfMonth}, ${date.year}"
}
/**
 * Card displaying availability details for a selected date
 */
@Composable
fun AvailabilityDetailsCard(
    selectedDate: LocalDate,
    availabilities: List<Availability>,
    onDeleteClick: (Availability) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formatDate(selectedDate),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (availabilities.isEmpty()) {
                Text(
                    text = "No availability set for this day",
                    style = MaterialTheme.typography.body1
                )
            } else {
                availabilities.forEach { availability ->
                    AvailabilityItem(
                        availability = availability,
                        onDeleteClick = { onDeleteClick(availability) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun AvailabilityItem(
    availability: Availability,
    onDeleteClick: () -> Unit
) {
    val startTimeStr = availability.startTime.toString().substringBefore("T").plus(" ").plus(
        availability.startTime.toString().substringAfter("T").substringBefore(".")
    )
    val endTimeStr = availability.endTime.toString().substringBefore("T").plus(" ").plus(
        availability.endTime.toString().substringAfter("T").substringBefore(".")
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
            )
            
            Text(
                text = "$startTimeStr - $endTimeStr",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.body1
            )
            
            Text(
                text = " (${calculateDuration(startTimeStr, endTimeStr)})",
                style = MaterialTheme.typography.caption
            )
        }
        
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Availability",
                tint = MaterialTheme.colors.error
            )
        }
    }
} 