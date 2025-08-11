package com.clockwise.features.shiftexchange.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import kotlinx.datetime.LocalDateTime
import com.clockwise.core.util.formatDate

@Composable
fun AvailableShiftCard(
    exchangeShift: ExchangeShift,
    onRequestShift: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Shift",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
                
                StatusChip(
                    text = exchangeShift.status.name.replace('_', ' '),
                    backgroundColor = when (exchangeShift.status) {
                        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.OPEN -> 
                            Color(0xFF4CAF50).copy(alpha = 0.1f)
                        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.PENDING_SELECTION -> 
                            Color(0xFFFF9800).copy(alpha = 0.1f)
                        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.AWAITING_MANAGER_APPROVAL -> 
                            Color(0xFF2196F3).copy(alpha = 0.1f)
                        else -> MaterialTheme.colors.primary.copy(alpha = 0.1f)
                    },
                    textColor = when (exchangeShift.status) {
                        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.OPEN -> 
                            Color(0xFF4CAF50)
                        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.PENDING_SELECTION -> 
                            Color(0xFFFF9800)
                        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.AWAITING_MANAGER_APPROVAL -> 
                            Color(0xFF2196F3)
                        else -> MaterialTheme.colors.primary
                    }
                )
            }
            
            // Shift details
            ShiftDetailRow(
                icon = Icons.Default.Person,
                label = "Posted by",
                value = exchangeShift.posterName
            )
            
            if (exchangeShift.position != null) {
                ShiftDetailRow(
                    icon = Icons.Default.Work,
                    label = "Position",
                    value = exchangeShift.position
                )
            }
            
            if (exchangeShift.shiftStartTime != null && exchangeShift.shiftEndTime != null) {
                ShiftDetailRow(
                    icon = Icons.Default.Schedule,
                    label = "Time",
                    value = formatShiftTime(exchangeShift.shiftStartTime, exchangeShift.shiftEndTime)
                )
            }
            
            // Request button
            Button(
                onClick = onRequestShift,
                modifier = Modifier.fillMaxWidth(),
                enabled = exchangeShift.canAcceptRequests()
            ) {
                Text(
                    text = if (exchangeShift.canAcceptRequests()) "Request This Shift" else "Unavailable"
                )
            }
        }
    }
}

@Composable
private fun ShiftDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colors.primary
        )
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = backgroundColor,
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.caption,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatShiftTime(startTime: LocalDateTime, endTime: LocalDateTime): String {
    val dateString = formatDate(startTime.date)
    val startTimeString = "${startTime.hour.toString().padStart(2, '0')}:${startTime.minute.toString().padStart(2, '0')}"
    val endTimeString = "${endTime.hour.toString().padStart(2, '0')}:${endTime.minute.toString().padStart(2, '0')}"
    
    return "$dateString • $startTimeString - $endTimeString"
}