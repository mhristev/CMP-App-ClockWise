package com.clockwise.user.presentation.home.schedule

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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Weekly Schedule",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4A2B8C)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Week days row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEach { day ->
                DayButton(
                    day = day,
                    isSelected = state.selectedDay == day,
                    onClick = { onAction(WeeklyScheduleAction.SelectDay(day)) }
                )
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
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(daySchedule) { shift ->
                            ShiftCard(shift = shift)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayButton(
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                if (isSelected) Color(0xFF4A2B8C)
                else Color.Transparent
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.name.take(3),
            color = if (isSelected) Color.White else Color(0xFF333333),
            style = MaterialTheme.typography.body2
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

            if (shift.employees.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
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
    val employees: List<String> = emptyList()
)

sealed interface WeeklyScheduleAction {
    object LoadWeeklySchedule : WeeklyScheduleAction
    data class SelectDay(val day: DayOfWeek) : WeeklyScheduleAction
}

data class WeeklyScheduleState(
    val weeklySchedule: Map<DayOfWeek, List<Shift>> = emptyMap(),
    val selectedDay: DayOfWeek? = null,
    val isLoading: Boolean = true
) 