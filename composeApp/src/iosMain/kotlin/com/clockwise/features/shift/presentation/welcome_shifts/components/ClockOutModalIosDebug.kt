package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

/**
 * iOS-specific debug version of ClockOutModal to help identify consumption items issues
 */
@Composable
fun ClockOutModalIosDebug(
    isVisible: Boolean,
    note: String,
    consumptionItems: List<ConsumptionItem>,
    selectedConsumptionItems: List<SelectedConsumptionItem>,
    selectedConsumptionType: String?,
    isLoadingConsumptionItems: Boolean,
    onNoteChange: (String) -> Unit,
    onConsumptionTypeSelected: (String?) -> Unit,
    onConsumptionItemQuantityChanged: (ConsumptionItem, Int) -> Unit,
    onClockOut: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        // Add iOS-specific debugging info
        println("iOS DEBUG ClockOutModal: isVisible = $isVisible")
        println("iOS DEBUG ClockOutModal: consumptionItems.size = ${consumptionItems.size}")
        println("iOS DEBUG ClockOutModal: isLoadingConsumptionItems = $isLoadingConsumptionItems")
        println("iOS DEBUG ClockOutModal: selectedConsumptionItems.size = ${selectedConsumptionItems.size}")
        
        // Use the regular ClockOutModal but with iOS-specific debugging
        ClockOutModal(
            isVisible = isVisible,
            note = note,
            consumptionItems = consumptionItems,
            selectedConsumptionItems = selectedConsumptionItems,
            selectedConsumptionType = selectedConsumptionType,
            isLoadingConsumptionItems = isLoadingConsumptionItems,
            onNoteChange = onNoteChange,
            onConsumptionTypeSelected = onConsumptionTypeSelected,
            onConsumptionItemQuantityChanged = onConsumptionItemQuantityChanged,
            onClockOut = onClockOut,
            onDismiss = onDismiss
        )
    }
}
