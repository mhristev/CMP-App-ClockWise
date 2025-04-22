package com.clockwise.features.shift.schedule.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.shift.core.domain.model.Shift
import com.clockwise.features.shift.core.presentation.theme.ShiftColors

@Composable
fun ShiftList(shifts: List<Shift>) {
    // Group shifts by position
    val shiftsByPosition = shifts.groupBy { it.position ?: "No Position" }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        shiftsByPosition.forEach { (position, positionShifts) ->
            item {
                // Position header
                Text(
                    text = position,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = ShiftColors.Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ShiftColors.Background)
                        .padding(8.dp)
                )
            }

            items(positionShifts) { shift ->
                ShiftCard(shift = shift)
            }
        }
    }
}