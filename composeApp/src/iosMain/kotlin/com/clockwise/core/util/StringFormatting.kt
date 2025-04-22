package com.clockwise.core.util

/**
 * iOS implementation of formatTimeString without using String.format
 */
actual fun formatTimeString(hour: Int, minute: Int): String {
    val formattedHour = hour.toString().padStart(2, '0')
    val formattedMinute = minute.toString().padStart(2, '0')
    return "$formattedHour:$formattedMinute"
} 