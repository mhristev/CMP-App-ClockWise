package com.clockwise.features.shift.schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.datetime.minus
import kotlinx.datetime.isoDayNumber

private fun formatDate(date: LocalDate): String {
    val monthName = when (date.month) {
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
    return "$monthName ${date.dayOfMonth}"
}

@Composable
fun WeeklyScheduleScreen(
    state: WeeklyScheduleState,
    onAction: (WeeklyScheduleAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(WeeklyScheduleAction.LoadWeeklySchedule)
        if (state.selectedDay == null) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
            onAction(WeeklyScheduleAction.SelectDay(today.dayOfWeek))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Schedule",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A2B8C)
            )
            
            Button(
                onClick = { onAction(WeeklyScheduleAction.NavigateToCurrentWeek) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF4A2B8C),
                    contentColor = Color.White
                )
            ) {
                Text("Today")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Week navigation and days row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(8.dp)
        ) {
            // Navigation arrows and current week
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAction(WeeklyScheduleAction.NavigateToPreviousWeek) }
                ) {
                    Text("←", color = Color(0xFF4A2B8C))
                }
                
                Text(
                    text = formatDate(state.currentWeekStart),
                    style = MaterialTheme.typography.subtitle1,
                    color = Color(0xFF4A2B8C)
                )
                
                IconButton(
                    onClick = { onAction(WeeklyScheduleAction.NavigateToNextWeek) }
                ) {
                    Text("→", color = Color(0xFF4A2B8C))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Week days with dates
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                var currentDate = state.currentWeekStart
                val today = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
                
                DayOfWeek.values().forEach { day ->
                    val isToday = currentDate == today
                    DayButtonWithDate(
                        day = day,
                        date = currentDate,
                        isSelected = state.selectedDay == day,
                        isToday = isToday,
                        onClick = { onAction(WeeklyScheduleAction.SelectDay(day)) }
                    )
                    currentDate = currentDate.plus(1, DateTimeUnit.DAY)
                }
            }
        }

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
        } else {
            state.selectedDay?.let { selectedDay ->
                Text(
                    text = selectedDay.name,
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val daySchedule = state.weeklySchedule[selectedDay] ?: emptyList()
                if (daySchedule.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No shifts scheduled",
                            style = MaterialTheme.typography.body1,
                            color = Color(0xFF666666)
                        )
                    }
                } else {
                    // Group shifts by position
                    val shiftsByPosition = daySchedule.groupBy { it.position ?: "No Position" }
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        shiftsByPosition.forEach { (position, shifts) ->
                            item {
                                // Position header
                                Text(
                                    text = position,
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A2B8C),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF5F5F5))
                                        .padding(8.dp)
                                )
                            }
                            
                            items(shifts) { shift ->
                                ShiftCard(shift = shift)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayButtonWithDate(
    day: DayOfWeek,
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(48.dp)
            .background(
                when {
                    isSelected -> Color(0xFF4A2B8C)
                    isToday -> Color(0xFFE6E0F3)  // Light purple for today
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.name.take(3),
            color = if (isSelected) Color.White else Color(0xFF333333),
            style = MaterialTheme.typography.body2,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isSelected) Color.White else if (isToday) Color(0xFF4A2B8C) else Color(0xFF666666),
            style = MaterialTheme.typography.caption,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
        )
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
                text = "${shift.startTime} - ${shift.endTime}",
                style = MaterialTheme.typography.body1,
                color = Color(0xFF333333)
            )

            if (shift.position != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Position: ${shift.position}",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
            }

            if (shift.employees.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Employees: ${shift.employees.joinToString(", ")}",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

data class Shift(
    val date: LocalDate,
    val startTime: String,
    val endTime: String,
    val position: String? = null,
    val employees: List<String> = emptyList()
)

sealed interface WeeklyScheduleAction {
    object LoadWeeklySchedule : WeeklyScheduleAction
    data class SelectDay(val day: DayOfWeek) : WeeklyScheduleAction
    object NavigateToNextWeek : WeeklyScheduleAction
    object NavigateToPreviousWeek : WeeklyScheduleAction
    object NavigateToCurrentWeek : WeeklyScheduleAction
}

data class WeeklyScheduleState(
    val weeklySchedule: Map<DayOfWeek, List<Shift>> = emptyMap(),
    val selectedDay: DayOfWeek? = Clock.System.now().toLocalDateTime(TimeZone.UTC).date.dayOfWeek,
    val currentWeekStart: LocalDate = getWeekStartDate(Clock.System.now().toLocalDateTime(TimeZone.UTC).date),
    val isLoading: Boolean = true
) 