package com.clockwise.features.shift.core.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.DayOfWeek

/**
 * Formats a LocalDateTime into a readable date string
 * Example: "Monday, January 1"
 */
fun formatDate(dateTime: LocalDateTime): String {
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

/**
 * Formats a LocalDateTime into a readable time string
 * Example: "9:30 AM"
 */
fun formatTime(dateTime: LocalDateTime): String {
    val hour = dateTime.hour
    val minute = dateTime.minute
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    
    val paddedMinute = if (minute < 10) "0$minute" else minute.toString()
    return "$displayHour:$paddedMinute $amPm"
} 