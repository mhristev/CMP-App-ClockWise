package com.clockwise.features.location.domain.model

sealed class LocationResult {
    data class Success(val location: Location) : LocationResult()
    data class Error(val message: String) : LocationResult()
    object Loading : LocationResult()
    object PermissionDenied : LocationResult()
    object LocationDisabled : LocationResult()
}
