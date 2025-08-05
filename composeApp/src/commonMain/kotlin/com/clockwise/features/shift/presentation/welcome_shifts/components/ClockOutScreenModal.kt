@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

@Composable
fun ClockOutScreenModal(
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
        // Use Popup instead of Dialog for better iOS compatibility
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            // Animated slide-in container
            var animationVisible by remember { mutableStateOf(false) }
            
            LaunchedEffect(isVisible) {
                animationVisible = isVisible
            }
            
            AnimatedVisibility(
                visible = animationVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Top App Bar
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Clock Out",
                                    style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = onDismiss) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Cancel",
                                        tint = Color.White
                                    )
                                }
                            },
                            backgroundColor = Color(0xFF4A2B8C),
                            contentColor = Color.White,
                            elevation = 4.dp
                        )
                        
                        // Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Summary Card
                            ClockOutSummaryCard(
                                selectedItems = selectedConsumptionItems,
                                note = note
                            )
                            
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
                        
                        // Bottom Action Bar
                        Surface(
                            elevation = 8.dp,
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                // Total cost if items selected
                                if (selectedConsumptionItems.isNotEmpty()) {
                                    val totalCost = selectedConsumptionItems.sumOf { it.totalPrice }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Total Cost:",
                                            style = MaterialTheme.typography.subtitle1,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "$${totalCost}",
                                            style = MaterialTheme.typography.h6,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1976D2)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                
                                // Clock Out Button
                                Button(
                                    onClick = onConfirmClockOut,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color(0xFF1976D2)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSaving
                                ) {
                                    if (isSaving) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                "Clocking Out...",
                                                color = Color.White,
                                                style = MaterialTheme.typography.subtitle1,
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
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Clock Out",
                                                color = Color.White,
                                                style = MaterialTheme.typography.subtitle1,
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
        }
    }
}