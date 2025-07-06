package com.clockwise.features.shift.presentation.welcome_shifts.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.clockwise.features.shift.domain.model.Shift
import com.clockwise.features.shift.domain.model.ShiftStatus
import com.clockwise.features.shift.presentation.welcome_shifts.WelcomeAction
import com.clockwise.core.util.formatDate
import com.clockwise.core.util.formatTime

@Composable
fun UpcomingShiftCard(
    shift: Shift,
    onAction: (WelcomeAction) -> Unit,
    canClockInOut: Boolean
) {
    var sessionNote by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(shift.startTime),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2B8C)
                )
                
                // Status chip
                Surface(
                    color = when (shift.status) {
                        ShiftStatus.SCHEDULED -> Color(0xFFE8E0F3)
                        ShiftStatus.CLOCKED_IN -> Color(0xFFE3F2FD)
                        ShiftStatus.COMPLETED -> Color(0xFFE8F5E9)
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (shift.status) {
                            ShiftStatus.SCHEDULED -> "Scheduled"
                            ShiftStatus.CLOCKED_IN -> "In Progress"
                            ShiftStatus.COMPLETED -> "Completed"
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = when (shift.status) {
                            ShiftStatus.SCHEDULED -> Color(0xFF4A2B8C)
                            ShiftStatus.CLOCKED_IN -> Color(0xFF1976D2)
                            ShiftStatus.COMPLETED -> Color(0xFF43A047)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scheduled time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Scheduled:",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "${formatTime(shift.startTime)} - ${formatTime(shift.endTime)}",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF333333)
                )
            }

            // Clock in time
            if (shift.clockInTime != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Clock in:",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = formatTime(shift.clockInTime),
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            // Clock out time
            if (shift.clockOutTime != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Clock out:",
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF666666)
                    )
                    Text(
                        text = formatTime(shift.clockOutTime),
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFF43A047)
                    )
                }
            }

            if (shift.position.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shift.position,
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
            }

            if (shift.status == ShiftStatus.CLOCKED_IN) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = sessionNote,
                    onValueChange = { sessionNote = it },
                    label = { Text("Session Note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        shift.workSession?.id?.let { workSessionId ->
                            onAction(WelcomeAction.SaveNote(workSessionId, sessionNote))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sessionNote.isNotBlank()
                ) {
                    Text("Save Note")
                }
            }


            if (canClockInOut) {
                Spacer(modifier = Modifier.height(12.dp))

                // Clock In/Out Button
                Button(
                    onClick = { 
                        when (shift.status) {
                            ShiftStatus.SCHEDULED -> onAction(WelcomeAction.ClockIn(shift.id))
                            ShiftStatus.CLOCKED_IN -> onAction(WelcomeAction.ClockOut(shift.id))
                            ShiftStatus.COMPLETED -> { /* Already completed */ }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = when (shift.status) {
                            ShiftStatus.SCHEDULED -> Color(0xFF4A2B8C)
                            ShiftStatus.CLOCKED_IN -> Color(0xFF1976D2)
                            ShiftStatus.COMPLETED -> Color(0xFF43A047)
                        }
                    ),
                    enabled = shift.status != ShiftStatus.COMPLETED
                ) {
                    Text(
                        text = when (shift.status) {
                            ShiftStatus.SCHEDULED -> "Clock In"
                            ShiftStatus.CLOCKED_IN -> "Clock Out"
                            ShiftStatus.COMPLETED -> "Completed"
                        },
                        color = Color.White
                    )
                }
            }
        }
    }
} 