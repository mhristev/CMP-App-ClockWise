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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

/**
 * iOS-optimized ClockOut screen that uses different strategies to handle
 * dialog presentation issues on iOS
 */
@Composable
fun ClockOutScreenOptimized(
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
    // Strategy 1: Use Popup instead of Dialog for better iOS compatibility
    if (isVisible) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            // Animated content inside Popup
            var showContent by remember { mutableStateOf(false) }
            
            LaunchedEffect(isVisible) {
                if (isVisible) {
                    showContent = true
                }
            }
            
            DisposableEffect(isVisible) {
                onDispose {
                    showContent = false
                }
            }
            
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 250,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    ClockOutContent(
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
    }
}

/**
 * Alternative implementation using Box overlay approach for maximum iOS compatibility
 */
@Composable
fun ClockOutScreenBoxOverlay(
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
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            ClockOutContent(
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

/**
 * Shared content component for all ClockOut screen variations
 */
@Composable
private fun ClockOutContent(
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