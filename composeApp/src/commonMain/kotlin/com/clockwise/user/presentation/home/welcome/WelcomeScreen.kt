package com.clockwise.user.presentation.home.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit

private fun formatDate(dateTime: LocalDateTime): String {
    val dayOfWeek = when (dateTime.dayOfWeek) {
        DayOfWeek.MONDAY -> "Monday"
        DayOfWeek.TUESDAY -> "Tuesday"
        DayOfWeek.WEDNESDAY -> "Wednesday"
        DayOfWeek.THURSDAY -> "Thursday"
        DayOfWeek.FRIDAY -> "Friday"
        DayOfWeek.SATURDAY -> "Saturday"
        DayOfWeek.SUNDAY -> "Sunday"
        else -> TODO()
    }
    
    val monthName = when (dateTime.month) {
        Month.JANUARY -> "January"
        Month.FEBRUARY -> "February"
        Month.MARCH -> "March"
        Month.APRIL -> "April"
        Month.MAY -> "May"
        Month.JUNE -> "June"
        Month.JULY -> "July"
        Month.AUGUST -> "August"
        Month.SEPTEMBER -> "September"
        Month.OCTOBER -> "October"
        Month.NOVEMBER -> "November"
        Month.DECEMBER -> "December"
        else -> TODO()
    }
    
    return "$dayOfWeek, $monthName ${dateTime.dayOfMonth}"
}

private fun formatTime(dateTime: LocalDateTime): String {
    val hour = dateTime.hour
    val minute = dateTime.minute
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    
    val paddedMinute = if (minute < 10) "0$minute" else minute.toString()
    return "$displayHour:$paddedMinute $amPm"
}

@Composable
fun WelcomeScreen(
    state: WelcomeState,
    onAction: (WelcomeAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(WelcomeAction.LoadUpcomingShifts)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome back!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A2B8C)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Shift Section
        Text(
            text = "Today's Shift",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4A2B8C)
                )
            }
        } else {
            state.todayShift?.let { shift ->
                ShiftCard(
                    shift = shift,
                    onAction = onAction,
                    canClockInOut = true
                )
            } ?: Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No shift scheduled for today",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF666666)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming Shifts Section
        Text(
            text = "Upcoming Shifts",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4A2B8C)
                )
            }
        } else if (state.upcomingShifts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No upcoming shifts",
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF666666)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.upcomingShifts) { shift ->
                    ShiftCard(
                        shift = shift,
                        onAction = onAction,
                        canClockInOut = false
                    )
                }
            }
        }
    }
}

@Composable
private fun ShiftCard(
    shift: Shift,
    onAction: (WelcomeAction) -> Unit,
    canClockInOut: Boolean
) {
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
                    text = formatDate(shift.date),
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

            if (shift.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shift.location,
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
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

data class Shift(
    val id: Int,
    val date: LocalDateTime,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String = "",
    val clockInTime: LocalDateTime? = null,
    val clockOutTime: LocalDateTime? = null,
    val status: ShiftStatus = ShiftStatus.SCHEDULED
)

enum class ShiftStatus {
    SCHEDULED,
    CLOCKED_IN,
    COMPLETED
}

sealed interface WelcomeAction {
    object LoadUpcomingShifts : WelcomeAction
    data class ClockIn(val shiftId: Int) : WelcomeAction
    data class ClockOut(val shiftId: Int) : WelcomeAction
}

data class WelcomeState(
    val todayShift: Shift? = null,
    val upcomingShifts: List<Shift> = emptyList(),
    val isLoading: Boolean = true
) 