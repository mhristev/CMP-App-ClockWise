package com.clockwise.core

import kotlinx.datetime.*

/**
 * Utility class for providing time-related functions that adapt to the user's current time zone.
 */
object TimeProvider {
    /**
     * Gets the current time in the local time zone
     */
    fun getCurrentLocalDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
    
    /**
     * Gets the current date in the local time zone
     */
    fun getCurrentLocalDate(): LocalDate {
        return getCurrentLocalDateTime().date
    }
    
    /**
     * Converts an instant to LocalDateTime in the user's current time zone
     */
    fun toLocalDateTime(instant: Instant): LocalDateTime {
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }
    
    /**
     * Converts a LocalDateTime to Instant using the user's current time zone
     */
    fun toInstant(localDateTime: LocalDateTime): Instant {
        return localDateTime.toInstant(TimeZone.currentSystemDefault())
    }
} 