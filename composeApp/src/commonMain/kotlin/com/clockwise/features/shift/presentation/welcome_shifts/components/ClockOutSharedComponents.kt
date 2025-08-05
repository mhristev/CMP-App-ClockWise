package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

@Composable
fun ClockOutSummaryCard(
    selectedItems: List<SelectedConsumptionItem>,
    note: String
) {
    // Staggered animation for card appearance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400))
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color(0xFFF8F9FA)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2B8C)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Note preview
                if (note.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Note: ",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = note.take(50) + if (note.length > 50) "..." else "",
                            style = MaterialTheme.typography.body2,
                            color = Color(0xFF333333),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Selected items preview
                if (selectedItems.isNotEmpty()) {
                    Text(
                        text = "Consumption Items (${selectedItems.size} selected):",
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF666666)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Show individual items
                    selectedItems.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• ${item.consumptionItem.name} x${item.quantity}",
                                style = MaterialTheme.typography.body2,
                                color = Color(0xFF333333),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$${item.totalPrice}",
                                style = MaterialTheme.typography.body2,
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    
                    val totalCost = selectedItems.sumOf { it.totalPrice }
                    if (totalCost > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Cost:",
                                style = MaterialTheme.typography.body2,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF666666)
                            )
                            Text(
                                text = "$${totalCost}",
                                style = MaterialTheme.typography.body2,
                                color = Color(0xFF1976D2),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No consumption items selected",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
fun SessionNoteSection(
    note: String,  
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Session Notes",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Text(
                text = "Add notes about your work session",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = "Enter session notes...",
                        color = Color(0xFF999999)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF4A2B8C),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                )
            )
        }
    }
}

@Composable
fun ConsumptionItemsCard(
    consumptionItems: List<ConsumptionItem>,
    selectedItems: List<SelectedConsumptionItem>,
    selectedType: String?,
    isLoading: Boolean,
    onTypeSelected: (String?) -> Unit,
    onItemQuantityChanged: (ConsumptionItem, Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Consumption Items",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            
            Text(
                text = "Select items consumed during your shift",
                style = MaterialTheme.typography.body2,
                color = Color(0xFF666666)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                isLoading -> {
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Loading items...",
                            style = MaterialTheme.typography.body2,
                            color = Color(0xFF666666)
                        )
                    }
                }
                consumptionItems.isNotEmpty() -> {
                    ConsumptionItemsSection(
                        items = consumptionItems,
                        selectedItems = selectedItems,
                        selectedType = selectedType,
                        onTypeSelected = onTypeSelected,
                        onItemQuantityChanged = onItemQuantityChanged,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                else -> {
                    Text(
                        text = "No consumption items available at this location",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}