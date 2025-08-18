package com.clockwise.features.managerapproval.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.clockwise.features.managerapproval.domain.model.PendingExchangeShift
import com.clockwise.features.managerapproval.domain.model.RequestType
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ManagerApprovalScreen(
    viewModel: ManagerApprovalViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pending Approvals",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { viewModel.onAction(ManagerApprovalAction.RefreshExchanges) },
                enabled = !state.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search Field
        TextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onAction(ManagerApprovalAction.SearchExchanges(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search by name or position") },
            leadingIcon = { 
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.onAction(ManagerApprovalAction.SearchExchanges("")) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            state.error != null -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colors.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.error ?: "Unknown error",
                            color = MaterialTheme.colors.error,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            state.filteredExchanges.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "No pending approvals",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (state.searchQuery.isEmpty()) {
                                "No pending approvals"
                            } else {
                                "No exchanges match your search"
                            },
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (state.searchQuery.isEmpty()) {
                                "All shift exchanges have been processed"
                            } else {
                                "Try adjusting your search terms"
                            },
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.filteredExchanges,
                        key = { it.id }
                    ) { exchange ->
                        ExchangeCard(
                            exchange = exchange,
                            onApprove = { 
                                viewModel.onAction(
                                    ManagerApprovalAction.ShowConfirmationDialog(
                                        requestId = exchange.requestId,
                                        isApproval = true
                                    )
                                )
                            },
                            onReject = { 
                                viewModel.onAction(
                                    ManagerApprovalAction.ShowConfirmationDialog(
                                        requestId = exchange.requestId,
                                        isApproval = false
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (state.showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ManagerApprovalAction.DismissConfirmationDialog) },
            title = {
                Text(
                    text = if (state.isApprovalAction) "Approve Exchange" else "Reject Exchange"
                )
            },
            text = {
                Text(
                    text = if (state.isApprovalAction) {
                        "Are you sure you want to approve this shift exchange?"
                    } else {
                        "Are you sure you want to reject this shift exchange?"
                    }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (state.isApprovalAction) {
                            viewModel.onAction(ManagerApprovalAction.ApproveExchange(state.selectedRequestId ?: ""))
                        } else {
                            viewModel.onAction(ManagerApprovalAction.RejectExchange(state.selectedRequestId ?: ""))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (state.isApprovalAction) MaterialTheme.colors.primary else MaterialTheme.colors.error
                    )
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colors.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = if (state.isApprovalAction) "Approve" else "Reject",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onAction(ManagerApprovalAction.DismissConfirmationDialog) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ExchangeCard(
    exchange: PendingExchangeShift,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with exchange type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (exchange.requestType) {
                        RequestType.TAKE_SHIFT -> "Take Shift"
                        RequestType.SWAP_SHIFT -> "Swap Shifts"
                    },
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                
                Icon(
                    imageVector = when (exchange.requestType) {
                        RequestType.TAKE_SHIFT -> Icons.Default.CallMade
                        RequestType.SWAP_SHIFT -> Icons.Default.SwapHoriz
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Poster info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Poster",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Posted by: ${exchange.posterUserFirstName} ${exchange.posterUserLastName}",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Requester info
            if (!exchange.requesterUserFirstName.isNullOrEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Requester",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Requested by: ${exchange.requesterUserFirstName} ${exchange.requesterUserLastName}",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Original shift info
            Text(
                text = "Original Shift",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Time",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatShiftTime(exchange.shiftStartTime, exchange.shiftEndTime),
                    style = MaterialTheme.typography.body2
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = "Position",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = exchange.shiftPosition ?: "Position not specified",
                    style = MaterialTheme.typography.body2
                )
            }
            
            // Swap shift info (if applicable)
            if (exchange.requestType == RequestType.SWAP_SHIFT && exchange.swapShiftId != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Swap With",
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Time",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatShiftTime(exchange.swapShiftStartTime, exchange.swapShiftEndTime),
                        style = MaterialTheme.typography.body2
                    )
                }
                
                if (!exchange.swapShiftPosition.isNullOrEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Work,
                            contentDescription = "Position",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = exchange.swapShiftPosition,
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colors.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reject")
                }
                
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Approve")
                }
            }
        }
    }
}

private fun formatShiftTime(startTime: Instant?, endTime: Instant?): String {
    return if (startTime != null && endTime != null) {
        val start = startTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val end = endTime.toLocalDateTime(TimeZone.currentSystemDefault())
        "${start.date} ${start.hour.toString().padStart(2, '0')}:${start.minute.toString().padStart(2, '0')} - ${end.hour.toString().padStart(2, '0')}:${end.minute.toString().padStart(2, '0')}"
    } else {
        "Time not available"
    }
}