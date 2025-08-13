package com.clockwise.features.shift.presentation.week_schedule.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.core.util.formatTimeString
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shift.presentation.theme.ShiftColors

@Composable
fun ShiftCard(shift: Shift) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Time range
            Text(
                text = "${formatTimeString(shift.startTime.hour, shift.startTime.minute)} - " +
                        "${formatTimeString(shift.endTime.hour, shift.endTime.minute)}",
                style = MaterialTheme.typography.h6,
                color = ShiftColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            // Position or role

            // Employee name or fallback to employeeId
            val displayName = when {
                shift.userFirstName != null && shift.userLastName != null -> 
                    "${shift.userFirstName} ${shift.userLastName}"
                shift.userFirstName != null -> shift.userFirstName
                shift.userLastName != null -> shift.userLastName
                else -> shift.employeeId
            }
            
            Text(
                text = displayName,
                style = MaterialTheme.typography.subtitle1,
                color = ShiftColors.TextSecondary
            )

            // Position or role
            shift.position?.let { position ->
                Text(
                    text = position,
                    style = MaterialTheme.typography.subtitle1,
                    color = ShiftColors.TextSecondary
                )
            }
        }
    }
} 