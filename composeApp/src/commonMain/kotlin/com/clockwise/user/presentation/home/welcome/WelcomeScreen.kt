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

        Text(
            text = "Upcoming Shifts",
            style = MaterialTheme.typography.h6,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4A2B8C)
                )
            }
        } else if (state.upcomingShifts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
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
                    ShiftCard(shift = shift)
                }
            }
        }
    }
}

@Composable
private fun ShiftCard(shift: Shift) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = formatDate(shift.date),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A2B8C)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${formatTime(shift.startTime)} - ${formatTime(shift.endTime)}",
                style = MaterialTheme.typography.body1,
                color = Color(0xFF333333)
            )

            if (shift.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = shift.location,
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

data class Shift(
    val date: LocalDateTime,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String = ""
)

sealed interface WelcomeAction {
    object LoadUpcomingShifts : WelcomeAction
}

data class WelcomeState(
    val upcomingShifts: List<Shift> = emptyList(),
    val isLoading: Boolean = true
) 