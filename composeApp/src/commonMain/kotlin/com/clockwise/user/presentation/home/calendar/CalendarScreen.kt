package com.clockwise.user.presentation.home.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun formatCurrentMonth(date: LocalDate): String {
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
    return "$monthName ${date.year}"
}

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
    return "$monthName ${date.dayOfMonth}, ${date.year}"
}

@Composable
fun CalendarScreen(
    state: CalendarState,
    onAction: (CalendarAction) -> Unit
) {
    LaunchedEffect(Unit) {
        onAction(CalendarAction.LoadMonthlySchedule)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A2B8C)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Month navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onAction(CalendarAction.NavigateToPreviousMonth) },
                   // enabled = isWithinAllowedRange(state.currentMonth, -3)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous month",
//                        tint = if (isWithinAllowedRange(state.currentMonth, -3))
//                            Color(0xFF4A2B8C) else Color.Gray
                    )
                }

                Text(
                    text = formatCurrentMonth(state.currentMonth),
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF333333)
                )

                IconButton(
                    onClick = { onAction(CalendarAction.NavigateToNextMonth) },
                   // enabled = isWithinAllowedRange(state.currentMonth, 50)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next month",
//                        tint = if (isWithinAllowedRange(state.currentMonth, 50))
//                            Color(0xFF4A2B8C) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Week days header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val weekDays = listOf(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
                )
                weekDays.forEach { day ->
                    Text(
                        text = day.name.take(3),
                        style = MaterialTheme.typography.body2,
                        color = Color(0xFF666666),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                // Calendar grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    items(getDaysInMonth(state.currentMonth)) { day ->
                        
                        DayCell(
                            day = day,
                            isSelected = state.selectedDate == day,
                            hasAvailability = state.monthlySchedule[day] != null,
                            currentMonth = state.currentMonth,
                            onClick = { onAction(CalendarAction.SelectDate(day)) }
                        )
                    }
                }
            }
        }

        // Floating Action Button for adding availability
        FloatingActionButton(
            onClick = { 
                if (state.selectedDate != null) {
                    onAction(CalendarAction.ShowAvailabilityDialog)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            backgroundColor = if (state.selectedDate != null) Color(0xFF4A2B8C) else Color(0xFFCCCCCC)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add availability",
                tint = Color.White
            )
        }

        // Availability dialog
        if (state.showAvailabilityDialog && state.selectedDate != null) {
            AvailabilityDialog(
                date = state.selectedDate,
                onDismiss = { onAction(CalendarAction.HideAvailabilityDialog) },
                onSetAvailability = { startTime, endTime ->
                    onAction(CalendarAction.SetAvailability(state.selectedDate, startTime, endTime))
                    onAction(CalendarAction.HideAvailabilityDialog)
                }
            )
        }
    }
}

@Composable
private fun DayCell(
    day: LocalDate,
    isSelected: Boolean,
    hasAvailability: Boolean,
    currentMonth: LocalDate,
    onClick: () -> Unit
) {
    val isCurrentMonth = day.month == currentMonth.month && day.year == currentMonth.year
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .background(
                when {
                    isSelected -> Color(0xFF4A2B8C)
                    hasAvailability -> Color(0xFFE8E0F3)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            color = when {
                isSelected -> Color.White
                hasAvailability -> Color(0xFF4A2B8C)
                !isCurrentMonth -> Color(0xFFCCCCCC)
                else -> Color(0xFF333333)
            },
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun AvailabilityDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onSetAvailability: (String, String) -> Unit
) {
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Availability for ${formatDate(date)}",
                color = Color(0xFF4A2B8C)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSetAvailability(startTime, endTime)
                    onDismiss()
                }
            ) {
                Text("Set", color = Color(0xFF4A2B8C))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF666666))
            }
        }
    )
}

private fun getDaysInMonth(currentMonth: LocalDate): List<LocalDate> {
    val firstDay = LocalDate(currentMonth.year, currentMonth.month, 1)
    val lastDay = when (currentMonth.month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY, Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (currentMonth.year % 4 == 0 && (currentMonth.year % 100 != 0 || currentMonth.year % 400 == 0)) 29 else 28
        else -> TODO()
    }
    
    // Get the first day of the week (Monday = 1, Sunday = 7)
    val firstDayOfWeek = firstDay.dayOfWeek.ordinal
    // Calculate days to add before to make Monday the first day (if firstDayOfWeek is 0 (Monday), we add 0 days)
    val daysToAddBefore = if (firstDayOfWeek == 0) 0 else firstDayOfWeek
    
    // Add days from previous month
    val daysBefore = (1..daysToAddBefore).map { day ->
        firstDay.minus(daysToAddBefore - day + 1, DateTimeUnit.DAY)
    }
    
    // Add days of current month
    val daysInCurrentMonth = (1..lastDay).map { day ->
        LocalDate(currentMonth.year, currentMonth.month, day)
    }
    
    // Calculate days needed from next month to complete the grid
    val totalDays = daysBefore.size + daysInCurrentMonth.size
    val daysNeededAfter = (7 - (totalDays % 7)) % 7
    val daysAfter = (1..daysNeededAfter).map { day ->
        LocalDate(currentMonth.year, currentMonth.month, lastDay).plus(day, DateTimeUnit.DAY)
    }
    
    // Debug print for the first day of the month
    println("First day of month: ${firstDay.dayOfMonth}/${firstDay.month}/${firstDay.year} is ${firstDay.dayOfWeek}")
    println("Days to add before: $daysToAddBefore")
    println("First day ordinal: ${firstDayOfWeek}")
    
    return daysBefore + daysInCurrentMonth + daysAfter
}

//private fun isWithinAllowedRange(date: LocalDate, monthsOffset: Int): Boolean {
//    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date
//    val targetDate = when {
//        monthsOffset > 0 -> date.plus(monthsOffset, DateTimeUnit.MONTH)
//        monthsOffset < 0 -> date.minus(-monthsOffset, DateTimeUnit.MONTH)
//        else -> date
//    }
//
//    val minDate = currentDate.minus(3, DateTimeUnit.MONTH)
//    val maxDate = currentDate.plus(3, DateTimeUnit.MONTH)
//
//    return targetDate >= minDate && targetDate <= maxDate
//}

sealed interface CalendarAction {
    object LoadMonthlySchedule : CalendarAction
    data class SelectDate(val date: LocalDate?) : CalendarAction
    data class SetAvailability(
        val date: LocalDate,
        val startTime: String,
        val endTime: String
    ) : CalendarAction
    object NavigateToNextMonth : CalendarAction
    object NavigateToPreviousMonth : CalendarAction
    object ShowAvailabilityDialog : CalendarAction
    object HideAvailabilityDialog : CalendarAction
}

data class CalendarState(
    val monthlySchedule: Map<LocalDate, Pair<String, String>> = emptyMap(),
    val selectedDate: LocalDate? = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val isLoading: Boolean = true,
    val currentMonth: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    val showAvailabilityDialog: Boolean = false
) 