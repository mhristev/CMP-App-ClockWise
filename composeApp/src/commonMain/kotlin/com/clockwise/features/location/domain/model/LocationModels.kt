package com.clockwise.features.location.domain.model

/**
 * Represents a geographical location with latitude, longitude, accuracy and timestamp
 */
data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long
) {
    companion object {
        /**
         * Calculates the distance between two locations using the Haversine formula
         * @param other The other location to calculate distance to
         * @return Distance in meters
         */
        fun Location.distanceTo(other: Location): Double {
            val earthRadius = 6371000.0 // Earth's radius in meters
            
            val lat1Rad = Math.toRadians(this.latitude)
            val lat2Rad = Math.toRadians(other.latitude)
            val deltaLatRad = Math.toRadians(other.latitude - this.latitude)
            val deltaLonRad = Math.toRadians(other.longitude - this.longitude)
            
            val a = kotlin.math.sin(deltaLatRad / 2) * kotlin.math.sin(deltaLatRad / 2) +
                    kotlin.math.cos(lat1Rad) * kotlin.math.cos(lat2Rad) *
                    kotlin.math.sin(deltaLonRad / 2) * kotlin.math.sin(deltaLonRad / 2)
            
            val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
            
            return earthRadius * c
        }
    }
}

/**
 * Sealed class representing the result of a location operation
 */
sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationDisabled : LocationResult()
    data class Error(val message: String) : LocationResult()
}

/**
 * Sealed class representing location permission status
 */
sealed class LocationPermissionResult {
    object Granted : LocationPermissionResult()
    object Denied : LocationPermissionResult()
    object PermanentlyDenied : LocationPermissionResult()
}
