@file:OptIn(ExperimentalMaterialApi::class)

package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun ConsumptionItemsSection(
    items: List<ConsumptionItem>,
    selectedItems: List<SelectedConsumptionItem>,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    onItemQuantityChanged: (ConsumptionItem, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val types = remember(items) {
        listOf("All") + items.map { it.type }.distinct().sorted()
    }
    
    val filteredItems = remember(items, selectedType) {
        if (selectedType == null || selectedType == "All") {
            items
        } else {
            items.filter { it.type == selectedType }
        }
    }
    
    Column(modifier = modifier) {
        // Type filter - only show if there are multiple types
        if (types.size > 2) {
            TypeFilterRow(
                types = types,
                selectedType = selectedType ?: "All",
                onTypeSelected = { type ->
                    onTypeSelected(if (type == "All") null else type)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Items list - use Column instead of LazyColumn to avoid nesting scroll conflicts
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredItems.forEach { item ->
                val selectedItem = selectedItems.find { it.consumptionItem.id == item.id }
                val quantity = selectedItem?.quantity ?: 0
                
                EnhancedConsumptionItemRow(
                    item = item,
                    quantity = quantity,
                    onQuantityChanged = { newQuantity ->
                        onItemQuantityChanged(item, newQuantity)
                    }
                )
            }
        }
    }
}

@Composable
private fun TypeFilterRow(
    types: List<String>,
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = { },
            readOnly = true,
            label = { Text("Filter by type") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                ) {
                    Text(text = type)
                }
            }
        }
    }
}

@Composable
private fun EnhancedConsumptionItemRow(
    item: ConsumptionItem,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = if (quantity > 0) 4.dp else 1.dp,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = if (quantity > 0) Color(0xFFF0F8FF) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Medium,
                    color = if (quantity > 0) Color(0xFF1976D2) else Color(0xFF333333)
                )
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
                if (quantity > 0) {
                    Text(
                        text = "Subtotal: $${item.price * quantity}",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Decrease button
                Card(
                    modifier = Modifier.size(40.dp),
                    elevation = if (quantity > 0) 2.dp else 0.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = if (quantity > 0) Color(0xFF4A2B8C) else Color(0xFFE0E0E0)
                ) {
                    IconButton(
                        onClick = {
                            if (quantity > 0) {
                                onQuantityChanged(quantity - 1)
                            }
                        },
                        enabled = quantity > 0
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.h6,
                            color = if (quantity > 0) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Quantity display
                Surface(
                    modifier = Modifier
                        .widthIn(min = 32.dp)
                        .padding(horizontal = 4.dp),
                    color = if (quantity > 0) Color(0xFF1976D2) else Color.Transparent,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (quantity > 0) Color.White else Color(0xFF333333),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Increase button
                Card(
                    modifier = Modifier.size(40.dp),
                    elevation = 2.dp,
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = Color(0xFF4A2B8C)
                ) {
                    IconButton(
                        onClick = {
                            onQuantityChanged(quantity + 1)
                        }
                    ) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.h6,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsumptionItemRow(
    item: ConsumptionItem,
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (quantity > 0) {
                            onQuantityChanged(quantity - 1)
                        }
                    },
                    enabled = quantity > 0
                ) {
                    Text(
                        text = "−",
                        style = MaterialTheme.typography.h5,
                        color = if (quantity > 0) Color(0xFF4A2B8C) else Color.Gray
                    )
                }
                
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 24.dp)
                )
                
                IconButton(
                    onClick = {
                        onQuantityChanged(quantity + 1)
                    }
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.h5,
                        color = Color(0xFF4A2B8C)
                    )
                }
            }
        }
    }
}