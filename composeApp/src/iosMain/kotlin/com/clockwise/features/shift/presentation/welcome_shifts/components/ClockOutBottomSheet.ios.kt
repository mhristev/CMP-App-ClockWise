@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons  
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun ClockOutBottomSheet(
    isVisible: Boolean,
    note: String,
    isSaving: Boolean,
    onNoteChange: (String) -> Unit,
    onConfirmClockOut: () -> Unit,
    onDismiss: () -> Unit,
    consumptionItems: List<ConsumptionItem>,
    selectedConsumptionItems: List<SelectedConsumptionItem>,
    selectedConsumptionType: String?,
    isLoadingConsumptionItems: Boolean,
    onConsumptionItemQuantityChanged: (ConsumptionItem, Int) -> Unit,
    onConsumptionTypeSelected: (String?) -> Unit
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            IOSSimpleScreen(
                note = note,
                isSaving = isSaving,
                onNoteChange = onNoteChange,
                onConfirmClockOut = onConfirmClockOut,
                onDismiss = onDismiss,
                consumptionItems = consumptionItems,
                selectedConsumptionItems = selectedConsumptionItems,
                selectedConsumptionType = selectedConsumptionType,
                isLoadingConsumptionItems = isLoadingConsumptionItems,
                onConsumptionItemQuantityChanged = onConsumptionItemQuantityChanged,
                onConsumptionTypeSelected = onConsumptionTypeSelected
            )
        }
    }
}

@Composable
private fun IOSSimpleScreen(
    note: String,
    isSaving: Boolean,
    onNoteChange: (String) -> Unit,
    onConfirmClockOut: () -> Unit,
    onDismiss: () -> Unit,
    consumptionItems: List<ConsumptionItem>,
    selectedConsumptionItems: List<SelectedConsumptionItem>,
    selectedConsumptionType: String?,
    isLoadingConsumptionItems: Boolean,
    onConsumptionItemQuantityChanged: (ConsumptionItem, Int) -> Unit,
    onConsumptionTypeSelected: (String?) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with cancel button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.surface,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel button
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colors.primary
                        )
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.body1
                        )
                    }
                    
                    // Title
                    Text(
                        text = "Clock Out",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colors.onSurface
                    )
                    
                    // Placeholder for balance
                    Spacer(modifier = Modifier.width(64.dp))
                }
            }
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Summary Card (if items selected)
                if (selectedConsumptionItems.isNotEmpty()) {
                    ClockOutSummaryCard(
                        selectedItems = selectedConsumptionItems,
                        note = note
                    )
                }
                
                // Session Note Section
                SessionNoteSection(
                    note = note,
                    onNoteChange = onNoteChange
                )
                
                // Consumption Items Section
                ConsumptionItemsCard(
                    consumptionItems = consumptionItems,
                    selectedItems = selectedConsumptionItems,
                    selectedType = selectedConsumptionType,
                    isLoading = isLoadingConsumptionItems,
                    onTypeSelected = onConsumptionTypeSelected,
                    onItemQuantityChanged = onConsumptionItemQuantityChanged
                )
            }
            
            // Bottom action area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.surface,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Total cost if items selected
                    if (selectedConsumptionItems.isNotEmpty()) {
                        val totalCost = selectedConsumptionItems.sumOf { it.totalPrice }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.12f),
                            elevation = 0.dp,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total Cost:",
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colors.onSurface
                                )
                                Text(
                                    text = "$${totalCost}",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colors.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Clock Out Button
                    Button(
                        onClick = onConfirmClockOut,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        enabled = !isSaving,
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp
                        )
                    ) {
                        if (isSaving) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colors.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Clocking Out...",
                                    color = MaterialTheme.colors.onPrimary,
                                    style = MaterialTheme.typography.button,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colors.onPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Clock Out",
                                    color = MaterialTheme.colors.onPrimary,
                                    style = MaterialTheme.typography.button,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}