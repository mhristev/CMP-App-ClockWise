package com.clockwise.features.availability.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import androidx.compose.material.Divider
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp), // Fixed height for the calendar grid
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
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Availability details section
                state.selectedDate?.let { selectedDate ->
                    val availability = state.monthlySchedule[selectedDate]
                    
                    if (availability != null) {
                        AvailabilityDetailsCard(
                            date = selectedDate,
                            startTime = availability.first,
                            endTime = availability.second,
                            onEdit = { 
                                // Prefill the dialog with existing values
                                onAction(CalendarAction.EditAvailability(selectedDate, availability.first, availability.second))
                            },
                            onDelete = { 
                                // Show delete confirmation
                                onAction(CalendarAction.ShowDeleteConfirmation(selectedDate))
                            }
                        )
                    } else {
                        // Show message when no availability is set for selected date
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No availability set for ${formatDate(selectedDate)}",
                                color = Color.Gray,
                                style = MaterialTheme.typography.body1
                            )
                        }
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

        // Availability dialog with preloaded values for editing
        if (state.showAvailabilityDialog && state.selectedDate != null) {
            // Check if we have existing availability for this date (editing)
            val existingAvailability = state.monthlySchedule[state.selectedDate]
            
            AvailabilityDialog(
                date = state.selectedDate,
                onDismiss = { onAction(CalendarAction.HideAvailabilityDialog) },
                onSetAvailability = { startTime, endTime ->
                    onAction(CalendarAction.SetAvailability(state.selectedDate, startTime, endTime))
                    onAction(CalendarAction.HideAvailabilityDialog)
                },
                initialStartTime = existingAvailability?.first ?: "",
                initialEndTime = existingAvailability?.second ?: ""
            )
        }

        // Delete confirmation dialog
        if (state.showDeleteConfirmationDialog && state.selectedDate != null) {
            DeleteConfirmationDialog(
                date = state.selectedDate,
                onDismiss = { onAction(CalendarAction.HideDeleteConfirmation) },
                onConfirmDelete = { 
                    onAction(CalendarAction.DeleteAvailability(state.selectedDate))
                    onAction(CalendarAction.HideDeleteConfirmation)
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
                    else -> Color.Transparent // Removed light purple background
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (hasAvailability && !isSelected) {
            // Draw a circle around days with availabilities
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF4A2B8C),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
        
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
private fun AvailabilityDetailsCard(
    date: LocalDate,
    startTime: String,
    endTime: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Availability Details",
                    style = MaterialTheme.typography.h6,
                    color = Color(0xFF4A2B8C),
                    fontWeight = FontWeight.Bold
                )
                
                // Action buttons
                Row {
                    // Edit button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit availability",
                            tint = Color(0xFF4A2B8C)
                        )
                    }
                    
                    // Delete button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete availability",
                            tint = Color(0xFFE53935) // Red color for delete
                        )
                    }
                }
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color(0xFFEEEEEE)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Date:",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(100.dp)
                )
                
                Text(
                    text = formatDate(date),
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF333333)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Start Time:",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(100.dp)
                )
                
                Text(
                    text = startTime,
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF333333)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "End Time:",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(100.dp)
                )
                
                Text(
                    text = endTime,
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF333333)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Duration:",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.width(100.dp)
                )
                
                // Calculate and display duration
                val duration = calculateDuration(startTime, endTime)
                Text(
                    text = duration,
                    style = MaterialTheme.typography.body1,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
private fun AvailabilityDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onSetAvailability: (String, String) -> Unit,
    initialStartTime: String = "",
    initialEndTime: String = ""
) {
    // Parse initial times to hour/minute values
    val (initialStartHour, initialStartMinute) = parseTimeString(initialStartTime)
    val (initialEndHour, initialEndMinute) = parseTimeString(initialEndTime)
    
    var startHour by remember { mutableStateOf(initialStartHour) }
    var startMinute by remember { mutableStateOf(initialStartMinute) }
    var endHour by remember { mutableStateOf(initialEndHour) }
    var endMinute by remember { mutableStateOf(initialEndMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialStartTime.isEmpty()) 
                    "Set Availability for ${formatDate(date)}"
                else 
                    "Edit Availability for ${formatDate(date)}",
                color = Color(0xFF4A2B8C),
                style = MaterialTheme.typography.h6
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Start Time Section
                Text(
                    text = "Start Time",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                TimePickerRow(
                    hour = startHour,
                    minute = startMinute,
                    onHourChange = { startHour = it },
                    onMinuteChange = { startMinute = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // End Time Section
                Text(
                    text = "End Time",
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                TimePickerRow(
                    hour = endHour,
                    minute = endMinute,
                    onHourChange = { endHour = it },
                    onMinuteChange = { endMinute = it }
                )
                
                // Duration Preview
                val startTimeFormatted = formatTime(startHour, startMinute)
                val endTimeFormatted = formatTime(endHour, endMinute)
                val duration = calculateDuration(startTimeFormatted, endTimeFormatted)
                
                if (duration != "Invalid duration") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Duration: $duration",
                        style = MaterialTheme.typography.caption,
                        color = Color(0xFF666666),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val startTimeFormatted = formatTime(startHour, startMinute)
                    val endTimeFormatted = formatTime(endHour, endMinute)
                    onSetAvailability(startTimeFormatted, endTimeFormatted)
                },
                enabled = isValidTimeRange(startHour, startMinute, endHour, endMinute)
            ) {
                Text(
                    text = if (initialStartTime.isEmpty()) "Set" else "Update", 
                    color = if (isValidTimeRange(startHour, startMinute, endHour, endMinute)) 
                        Color(0xFF4A2B8C) else Color(0xFFCCCCCC)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF666666))
            }
        }
    )
}

@Composable
private fun TimePickerRow(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hour Picker
        TimePicker(
            label = "Hour",
            value = hour,
            range = 0..23,
            onValueChange = onHourChange,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = ":",
            style = MaterialTheme.typography.h5,
            color = Color(0xFF333333),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        // Minute Picker
        TimePicker(
            label = "Min",
            value = minute,
            range = 0..59,
            step = 15, // 15-minute intervals
            onValueChange = onMinuteChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimePicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    step: Int = 1
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = Color(0xFF666666)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Decrease button
            IconButton(
                onClick = {
                    val newValue = if (value - step < range.first) range.last else value - step
                    onValueChange(newValue)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrease $label",
                    tint = Color(0xFF4A2B8C)
                )
            }
            
            // Value display
            Text(
                text = value.toString().padStart(2, '0'),
                style = MaterialTheme.typography.h6,
                color = Color(0xFF333333),
                modifier = Modifier
                    .background(
                        Color(0xFFF5F5F5),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .width(48.dp),
                textAlign = TextAlign.Center
            )
            
            // Increase button
            IconButton(
                onClick = {
                    val newValue = if (step == 15) {
                        // For minutes with 15-minute intervals
                        val nextValue = ((value / 15) + 1) * 15
                        if (nextValue > range.last) 0 else nextValue
                    } else {
                        if (value + step > range.last) range.first else value + step
                    }
                    onValueChange(newValue)
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Increase $label",
                    tint = Color(0xFF4A2B8C)
                )
            }
        }
    }
}

// Helper functions
private fun parseTimeString(timeString: String): Pair<Int, Int> {
    if (timeString.isEmpty()) return Pair(9, 0) // Default to 9:00 AM
    
    return try {
        val parts = timeString.split(":")
        if (parts.size >= 2) {
            val hour = parts[0].toIntOrNull() ?: 9
            val minute = parts[1].toIntOrNull() ?: 0
            Pair(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
        } else {
            Pair(9, 0)
        }
    } catch (e: Exception) {
        Pair(9, 0)
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

private fun isValidTimeRange(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Boolean {
    val startTotalMinutes = startHour * 60 + startMinute
    val endTotalMinutes = endHour * 60 + endMinute
    return endTotalMinutes > startTotalMinutes
}

@Composable
private fun DeleteConfirmationDialog(
    date: LocalDate,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Availability",
                color = Color(0xFF4A2B8C)
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete your availability for ${formatDate(date)}?",
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmDelete
            ) {
                Text("Delete", color = Color(0xFFE53935))
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

// Helper function to calculate the duration between two time strings (HH:mm format)
private fun calculateDuration(startTime: String, endTime: String): String {
    try {
        val (startHour, startMinute) = startTime.split(":").map { it.toInt() }
        val (endHour, endMinute) = endTime.split(":").map { it.toInt() }
        
        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute
        val durationMinutes = endTotalMinutes - startTotalMinutes
        
        val hours = durationMinutes / 60
        val minutes = durationMinutes % 60
        
        return "${hours}h ${minutes}m"
    } catch (e: Exception) {
        return "Invalid duration"
    }
}
