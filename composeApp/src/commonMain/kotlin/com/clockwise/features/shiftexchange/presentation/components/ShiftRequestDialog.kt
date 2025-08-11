package com.clockwise.features.shiftexchange.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shiftexchange.domain.model.ExchangeShift
import com.clockwise.features.shiftexchange.domain.model.RequestType
import kotlinx.datetime.LocalDateTime
import com.clockwise.core.util.formatDate

@Composable
fun ShiftRequestDialog(
    exchangeShift: ExchangeShift,
    userShifts: List<Shift>,
    onSubmitRequest: (RequestType, String?, String?, String?, String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var requestType by remember { mutableStateOf<RequestType?>(null) }
    var selectedSwapShift by remember { mutableStateOf<Shift?>(null) }
    
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
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Request Shift",
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
                            text = "Requesting shift from ${exchangeShift.posterName}",
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
                
                // Request type selection
                Text(
                    text = "How would you like to request this shift?",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onSurface
                )
                
                RequestTypeCard(
                    type = RequestType.TAKE_SHIFT,
                    title = "Take Shift",
                    description = "Take this shift without offering one in return",
                    isSelected = requestType == RequestType.TAKE_SHIFT,
                    onSelect = { 
                        requestType = RequestType.TAKE_SHIFT
                        selectedSwapShift = null
                    }
                )
                
                RequestTypeCard(
                    type = RequestType.SWAP_SHIFT,
                    title = "Swap Shifts",
                    description = "Offer one of your shifts in exchange",
                    isSelected = requestType == RequestType.SWAP_SHIFT,
                    onSelect = { 
                        requestType = RequestType.SWAP_SHIFT
                    }
                )
                
                // Swap shift selection (only show if swap is selected)
                if (requestType == RequestType.SWAP_SHIFT) {
                    Text(
                        text = "Select your shift to offer in exchange:",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface
                    )
                    
                    if (userShifts.isEmpty()) {
                        Card(
                            backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f),
                            elevation = 0.dp
                        ) {
                            Text(
                                text = "You don't have any shifts to offer for swap",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 150.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(userShifts) { shift ->
                                SwapShiftCard(
                                    shift = shift,
                                    isSelected = selectedSwapShift?.id == shift.id,
                                    onSelect = { selectedSwapShift = shift }
                                )
                            }
                        }
                    }
                }
                
                Divider()
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = { 
                            requestType?.let { type ->
                                val swapShiftId = if (type == RequestType.SWAP_SHIFT) selectedSwapShift?.id else null
                                val swapShiftPosition = if (type == RequestType.SWAP_SHIFT) selectedSwapShift?.position else null
                                val swapShiftStartTime = if (type == RequestType.SWAP_SHIFT) "${selectedSwapShift?.startTime}:00Z" else null
                                val swapShiftEndTime = if (type == RequestType.SWAP_SHIFT) "${selectedSwapShift?.endTime}:00Z" else null
                                onSubmitRequest(type, swapShiftId, swapShiftPosition, swapShiftStartTime, swapShiftEndTime)
                            }
                        },
                        enabled = requestType != null && 
                                 (requestType != RequestType.SWAP_SHIFT || selectedSwapShift != null),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Submit Request")
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestTypeCard(
    type: RequestType,
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        elevation = if (isSelected) 4.dp else 1.dp,
        backgroundColor = if (isSelected) {
            MaterialTheme.colors.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colors.surface
        },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colors.primary
                )
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.caption,
                    color = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.8f) else MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SwapShiftCard(
    shift: Shift,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        elevation = if (isSelected) 2.dp else 0.dp,
        backgroundColor = if (isSelected) {
            MaterialTheme.colors.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colors.surface
        },
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                modifier = Modifier.size(16.dp),
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colors.primary
                )
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = shift.position,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
                )
                Text(
                    text = formatShiftTime(shift.startTime, shift.endTime),
                    style = MaterialTheme.typography.caption,
                    color = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.7f) else MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private fun formatShiftTime(startTime: LocalDateTime, endTime: LocalDateTime): String {
    val dateString = formatDate(startTime.date)
    val startTimeString = "${startTime.hour.toString().padStart(2, '0')}:${startTime.minute.toString().padStart(2, '0')}"
    val endTimeString = "${endTime.hour.toString().padStart(2, '0')}:${endTime.minute.toString().padStart(2, '0')}"
    
    return "$dateString • $startTimeString - $endTimeString"
}