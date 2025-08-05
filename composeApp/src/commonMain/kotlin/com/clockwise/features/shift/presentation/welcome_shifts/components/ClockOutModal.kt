package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.clockwise.features.shift.presentation.welcome_shifts.WelcomeAction
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

@Composable
fun ClockOutModal(
    isVisible: Boolean,
    note: String,
    isSaving: Boolean,
    onNoteChange: (String) -> Unit,
    onConfirmClockOut: () -> Unit,
    onDismiss: () -> Unit,
    // Consumption items parameters
    consumptionItems: List<ConsumptionItem> = emptyList(),
    selectedConsumptionItems: List<SelectedConsumptionItem> = emptyList(),
    selectedConsumptionType: String? = null,
    isLoadingConsumptionItems: Boolean = false,
    onConsumptionItemQuantityChanged: (ConsumptionItem, Int) -> Unit = { _, _ -> },
    onConsumptionTypeSelected: (String?) -> Unit = { }
) {
    if (isVisible) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp),
                elevation = 8.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Clock Out",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A2B8C)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Scrollable content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Session Note Section
                        Text(
                            text = "Add a session note (optional):",
                            style = MaterialTheme.typography.body1,
                            color = Color(0xFF333333)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = note,
                            onValueChange = onNoteChange,
                            label = { Text("Session Note") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            placeholder = { 
                                Text(
                                    "Add details about your work session...\n\n• Tasks completed\n• Issues encountered\n• Notes for next shift\n• Break times", 
                                    color = Color(0xFF888888)
                                ) 
                            },
                            singleLine = false,
                            maxLines = 5
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Consumption Items Section
                        // Debug logging for consumption items
                        println("DEBUG ClockOutModal: consumptionItems.size = ${consumptionItems.size}")
                        println("DEBUG ClockOutModal: isLoadingConsumptionItems = $isLoadingConsumptionItems")
                        println("DEBUG ClockOutModal: selectedConsumptionItems.size = ${selectedConsumptionItems.size}")
                        
                        // Always show consumption items section for debugging
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isLoadingConsumptionItems) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF4A2B8C)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Loading consumption items...",
                                    style = MaterialTheme.typography.body2,
                                    color = Color(0xFF666666)
                                )
                            }
                        } else if (consumptionItems.isNotEmpty()) {
                            ConsumptionItemsSection(
                                items = consumptionItems,
                                selectedItems = selectedConsumptionItems,
                                selectedType = selectedConsumptionType,
                                onTypeSelected = onConsumptionTypeSelected,
                                onItemQuantityChanged = onConsumptionItemQuantityChanged,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // Show message when no consumption items are available
                            Text(
                                text = "No consumption items available at this location",
                                style = MaterialTheme.typography.body2,
                                color = Color(0xFF666666),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isSaving
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color(0xFF4A2B8C)
                            )
                        }
                        
                        Button(
                            onClick = onConfirmClockOut,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF1976D2)
                            ),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Clocking Out...", color = Color.White)
                                }
                            } else {
                                Text("Clock Out", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
} 