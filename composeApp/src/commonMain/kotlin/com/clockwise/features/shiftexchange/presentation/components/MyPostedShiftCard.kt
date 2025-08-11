package com.clockwise.features.shiftexchange.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import kotlinx.datetime.LocalDateTime
import com.clockwise.core.util.formatDate

@Composable
fun MyPostedShiftCard(
    exchangeShift: ExchangeShift,
    requests: List<ShiftRequest>,
    onViewRequests: () -> Unit,
    onCancelShift: () -> Unit,
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
                    text = "Your Posted Shift",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )
                
                StatusChip(
                    text = exchangeShift.status.name.replace('_', ' '),
                    backgroundColor = getStatusColor(exchangeShift.status).copy(alpha = 0.1f),
                    textColor = getStatusColor(exchangeShift.status)
                )
            }
            
            // Shift details
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
            
            // Requests count
            ShiftDetailRow(
                icon = Icons.Default.People,
                label = "Requests",
                value = "${requests.size} ${if (requests.size == 1) "request" else "requests"}"
            )
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (requests.isNotEmpty()) {
                    Button(
                        onClick = onViewRequests,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Requests (${requests.size})")
                    }
                }
                
                if (exchangeShift.canBeModifiedByPoster()) {
                    OutlinedButton(
                        onClick = onCancelShift,
                        modifier = if (requests.isEmpty()) Modifier.fillMaxWidth() else Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cancel")
                    }
                }
            }
            
            // Status message
            when (exchangeShift.status) {
                com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.AWAITING_MANAGER_APPROVAL -> {
                    Card(
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        elevation = 0.dp
                    ) {
                        Text(
                            text = "A request has been accepted and is awaiting manager approval",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
                com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.APPROVED -> {
                    Card(
                        backgroundColor = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        elevation = 0.dp
                    ) {
                        Text(
                            text = "Shift exchange has been approved by manager",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.caption,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
                com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.REJECTED -> {
                    Card(
                        backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                        elevation = 0.dp
                    ) {
                        Text(
                            text = "Shift exchange was rejected by manager",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.error
                        )
                    }
                }
                else -> { /* No message for other statuses */ }
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

private fun getStatusColor(status: com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus): Color {
    return when (status) {
        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.OPEN -> Color(0xFF4CAF50)
        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.PENDING_SELECTION -> Color(0xFFFF9800)
        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.AWAITING_MANAGER_APPROVAL -> Color(0xFF2196F3)
        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.APPROVED -> Color(0xFF4CAF50)
        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.REJECTED -> Color(0xFFF44336)
        com.clockwise.features.shiftexchange.domain.model.ExchangeShiftStatus.CANCELLED -> Color(0xFF9E9E9E)
    }
}

private fun formatShiftTime(startTime: LocalDateTime, endTime: LocalDateTime): String {
    val dateString = formatDate(startTime.date)
    val startTimeString = "${startTime.hour.toString().padStart(2, '0')}:${startTime.minute.toString().padStart(2, '0')}"
    val endTimeString = "${endTime.hour.toString().padStart(2, '0')}:${endTime.minute.toString().padStart(2, '0')}"
    
    return "$dateString • $startTimeString - $endTimeString"
}