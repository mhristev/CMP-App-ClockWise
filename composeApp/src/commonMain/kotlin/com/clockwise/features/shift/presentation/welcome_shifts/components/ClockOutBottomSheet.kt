package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.runtime.Composable
import com.clockwise.features.consumption.domain.model.ConsumptionItem
import com.clockwise.features.consumption.domain.model.SelectedConsumptionItem

@Composable
expect fun ClockOutBottomSheet(
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
)