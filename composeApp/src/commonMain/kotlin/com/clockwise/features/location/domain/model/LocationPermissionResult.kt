package com.clockwise.features.location.domain.model

sealed class LocationPermissionResult {
    object Granted : LocationPermissionResult()
    object Denied : LocationPermissionResult()
    object ShowRationale : LocationPermissionResult()
}