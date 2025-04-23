package com.clockwise.features.availability.presentation.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.features.availability.domain.model.Availability
import kotlinx.datetime.LocalDate

/**
 * Composable for rendering an individual day cell in the calendar
 */
@Composable
fun DayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    hasAvailability: Boolean,
    availabilityForDate: List<Availability>,
    onDateClick: (LocalDate) -> Unit,
    currentMonth: LocalDate
) {
    val textColor = when {
        isSelected -> Color.White
        !isCurrentMonth -> Color.Gray
        else -> MaterialTheme.colors.onSurface
    }
    
    val backgroundColor = when {
        isSelected -> MaterialTheme.colors.primary
        else -> Color.Transparent
    }
    
    val borderColor = when {
        hasAvailability -> MaterialTheme.colors.primary
        else -> Color.Transparent
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (hasAvailability && !isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable { onDateClick(date) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = textColor,
            textAlign = TextAlign.Center
        )
        
        // Show a dot indicator if there's availability
        if (hasAvailability && !isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
            )
        }
    }
} 