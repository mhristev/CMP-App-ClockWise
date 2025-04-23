package com.clockwise.features.shift.domain.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus

/**
 * Format a date into a readable format (e.g., "January 15")
 */
fun formatDate(date: LocalDate): String {
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
        else -> throw IllegalArgumentException("Unknown month: ${date.month}")
    }
    return "$monthName ${date.dayOfMonth}"
}

/**
 * Calculate the first day (Monday) of the week containing the given date
 */
fun getWeekStartDate(date: LocalDate): LocalDate {
    // In ISO-8601, Monday is 1 and Sunday is 7
    val dayOfWeek = date.dayOfWeek.isoDayNumber
    // Calculate how many days to go back to reach Monday
    val daysToSubtract = dayOfWeek - 1
    return date.minus(daysToSubtract, DateTimeUnit.DAY)
} 