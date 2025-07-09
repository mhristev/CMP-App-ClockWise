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
    
    /**
     * Parses an ISO-8601 date-time string with timezone information into a LocalDateTime
     */
    fun parseIsoDateTime(isoString: String): LocalDateTime {
        // First parse to Instant which handles timezone offsets
        val instant = Instant.parse(isoString)
        // Then convert to LocalDateTime in the user's timezone
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }
    
    /**
     * Formats LocalDateTime for backend API calls (without timezone info)
     * Format: yyyy-MM-dd'T'HH:mm:ss
     */
    fun formatForBackendApi(localDateTime: LocalDateTime): String {
        val year = localDateTime.year
        val month = localDateTime.monthNumber.toString().padStart(2, '0')
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        val second = localDateTime.second.toString().padStart(2, '0')
        
        return "${year}-${month}-${day}T${hour}:${minute}:${second}"
    }

    /**
     * Formats LocalDateTime as ISO-8601 string with timezone for API calls.
     * This is the correct format to use for availability API calls.
     */
    fun formatIsoDateTime(localDateTime: LocalDateTime): String {
        // Convert LocalDateTime to UTC before formatting with Z timezone indicator
        val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())
        val utcDateTime = instant.toLocalDateTime(TimeZone.UTC)
        
        val year = utcDateTime.year
        val month = utcDateTime.monthNumber.toString().padStart(2, '0')
        val day = utcDateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = utcDateTime.hour.toString().padStart(2, '0')
        val minute = utcDateTime.minute.toString().padStart(2, '0')
        val second = utcDateTime.second.toString().padStart(2, '0')
        
        return "${year}-${month}-${day}T${hour}:${minute}:${second}Z"
    }
    
    /**
     * Extracts the date part from an ISO-8601 date-time string
     */
    fun extractDateFromIsoString(isoString: String): LocalDate {
        return parseIsoDateTime(isoString).date
    }
    
    /**
     * Converts epoch seconds (with optional fractional part) to LocalDateTime
     */
    fun epochSecondsToLocalDateTime(epochSeconds: Double): LocalDateTime {
        val seconds = epochSeconds.toLong()
        val nanos = ((epochSeconds - seconds) * 1_000_000_000).toLong()
        val instant = Instant.fromEpochSeconds(seconds, nanos)
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    /**
     * Overloaded function to handle epoch seconds as a String.
     */
    fun epochSecondsToLocalDateTime(epochSeconds: String): LocalDateTime {
        return epochSecondsToLocalDateTime(epochSeconds.toDouble())
    }
    
    /**
     * Converts LocalDateTime to epoch seconds
     */
    fun localDateTimeToEpochSeconds(localDateTime: LocalDateTime): Double {
        val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())
        return instant.epochSeconds + (instant.nanosecondsOfSecond / 1_000_000_000.0)
    }
    
    /**
     * Returns the local timezone offset in ISO format (e.g., +03:00 or -05:00)
     */
    fun getLocalTimezoneOffset(): String {
        val now = Clock.System.now()
        val offsetSeconds = TimeZone.currentSystemDefault().offsetAt(now).totalSeconds
        
        // Convert to hours and minutes
        val hours = offsetSeconds / 3600
        val minutes = (kotlin.math.abs(offsetSeconds) % 3600) / 60
        
        val sign = if (hours >= 0) "+" else "-"
        return "$sign${kotlin.math.abs(hours).toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
    }

    /**
     * Converts a ZonedDateTime string to LocalDateTime.
     */
    fun zonedDateTimeStringToLocalDateTime(zonedDateTimeString: String): LocalDateTime {
        return Instant.parse(zonedDateTimeString).toLocalDateTime(TimeZone.currentSystemDefault())
    }

    /**
     * Converts a LocalDateTime to ZonedDateTime string.
     */
    fun localDateTimeToZonedDateTimeString(localDateTime: LocalDateTime): String {
        return localDateTime.toInstant(TimeZone.currentSystemDefault()).toString()
    }
}