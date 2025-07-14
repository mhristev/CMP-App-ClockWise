package com.clockwise.features.clockin.domain.model

import com.clockwise.features.location.domain.model.ClockInEligibility

/**
 * Represents a clock-in request with location validation
 */
data class ClockInRequest(
    val userId: String,
    val businessUnitId: String,
    val timestamp: Long,
    val location: com.clockwise.features.location.domain.model.Location?,
    val isEligible: Boolean
)

/**
 * Represents the result of a clock-in attempt
 */
sealed class ClockInResult {
    data class Success(val message: String) : ClockInResult()
    data class LocationNotEligible(val eligibility: ClockInEligibility) : ClockInResult()
    data class NetworkError(val message: String) : ClockInResult()
    data class Error(val message: String) : ClockInResult()
}
