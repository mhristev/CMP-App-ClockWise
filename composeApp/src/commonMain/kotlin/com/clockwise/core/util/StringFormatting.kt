package com.clockwise.core.util

/**
 * Platform-specific implementation of String.format
 * This is needed because String.format is not available in Kotlin/Native
 */
expect fun formatTimeString(hour: Int, minute: Int): String 