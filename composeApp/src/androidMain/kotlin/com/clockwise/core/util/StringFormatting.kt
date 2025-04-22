package com.clockwise.core.util

/**
 * Android implementation of formatTimeString using String.format
 */
actual fun formatTimeString(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
} 