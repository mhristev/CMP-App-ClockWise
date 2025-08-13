package com.clockwise.features.shiftexchange.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.RequestStatus
import com.clockwise.features.shiftexchange.domain.model.RequestType
import com.clockwise.features.shiftexchange.domain.model.ShiftRequest
import kotlinx.datetime.LocalDateTime
import com.clockwise.core.util.formatDate

@Composable
fun ViewRequestsDialog(
    exchangeShift: ExchangeShift,
    requests: List<ShiftRequest>,
    onAcceptRequest: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp), // Set maximum height to ensure dialog fits on screen
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header - Fixed at top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shift Requests",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onSurface
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Divider()
                
                // Scrollable content section
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Shift info
                    Card(
                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                        elevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Your Posted Shift",
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colors.onSurface
                            )
                            
                            if (exchangeShift.position != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Work,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colors.primary
                                    )
                                    Text(
                                        text = exchangeShift.position,
                                        style = MaterialTheme.typography.body2,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                            
                            if (exchangeShift.shiftStartTime != null && exchangeShift.shiftEndTime != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colors.primary
                                    )
                                    Text(
                                        text = formatShiftTime(exchangeShift.shiftStartTime, exchangeShift.shiftEndTime),
                                        style = MaterialTheme.typography.body2,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Requests
                    if (requests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No requests yet",
                                    style = MaterialTheme.typography.body1,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Requests will appear here when other employees are interested in your shift",
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "${requests.size} ${if (requests.size == 1) "Request" else "Requests"}",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colors.onSurface
                        )
                        
                        // Replace LazyColumn with regular Column for better scrolling integration
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            requests.forEach { request ->
                                ShiftRequestCard(
                                    request = request,
                                    onAccept = { onAcceptRequest(request.id) }
                                )
                            }
                        }
                    }
                }
                
                // Close button - Fixed at bottom
                Divider(modifier = Modifier.padding(top = 16.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun ShiftRequestCard(
    request: ShiftRequest,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Request header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.primary
                    )
                    Text(
                        text = request.requesterName,
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface
                    )
                }
                
                StatusChip(
                    text = request.status.name.replace('_', ' '),
                    backgroundColor = getRequestStatusColor(request.status).copy(alpha = 0.1f),
                    textColor = getRequestStatusColor(request.status)
                )
            }
            
            // Request type
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (request.requestType == RequestType.SWAP_SHIFT) 
                        Icons.Default.SwapHoriz else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = if (request.requestType == RequestType.SWAP_SHIFT) {
                        "Wants to swap shifts"
                    } else {
                        "Wants to take this shift"
                    },
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                )
            }
            
            // Swap shift details (if applicable)
            if (request.requestType == RequestType.SWAP_SHIFT && 
                request.swapShiftStartTime != null && 
                request.swapShiftEndTime != null) {
                Card(
                    backgroundColor = MaterialTheme.colors.surface,
                    elevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Offering in return:",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                        
                        if (request.swapShiftPosition != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Work,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = request.swapShiftPosition,
                                    style = MaterialTheme.typography.body2,
                                    color = MaterialTheme.colors.onSurface
                                )
                            }
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = formatShiftTime(request.swapShiftStartTime, request.swapShiftEndTime),
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Action button
            if (request.isPending()) {
                Button(
                    onClick = onAccept,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accept Request")
                }
            }
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

private fun getRequestStatusColor(status: RequestStatus): Color {
    return when (status) {
        RequestStatus.PENDING -> Color(0xFF4CAF50)
        RequestStatus.ACCEPTED_BY_POSTER -> Color(0xFF2196F3)
        RequestStatus.DECLINED_BY_POSTER -> Color(0xFFF44336)
        RequestStatus.APPROVED_BY_MANAGER -> Color(0xFF4CAF50)
        RequestStatus.REJECTED_BY_MANAGER -> Color(0xFFF44336)
    }
}

private fun formatShiftTime(startTime: LocalDateTime, endTime: LocalDateTime): String {
    val dateString = formatDate(startTime.date)
    val startTimeString = "${startTime.hour.toString().padStart(2, '0')}:${startTime.minute.toString().padStart(2, '0')}"
    val endTimeString = "${endTime.hour.toString().padStart(2, '0')}:${endTime.minute.toString().padStart(2, '0')}"
    
    return "$dateString • $startTimeString - $endTimeString"
}