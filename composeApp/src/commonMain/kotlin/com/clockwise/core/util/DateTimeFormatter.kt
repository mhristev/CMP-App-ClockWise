package com.clockwise.core.util

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
 * Example: "09:30"
 */
fun formatTime(dateTime: LocalDateTime): String {
    val hour = dateTime.hour
    val minute = dateTime.minute
    
    val paddedHour = if (hour < 10) "0$hour" else hour.toString()
    val paddedMinute = if (minute < 10) "0$minute" else minute.toString()
    
    return "$paddedHour:$paddedMinute"
} 