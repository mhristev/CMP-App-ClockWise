package com.clockwise.features.location.domain.usecase

import com.clockwise.features.location.domain.model.LocationPermissionResult
import com.clockwise.features.location.domain.repository.LocationRepository

/**
 * Use case for checking location permission status
 */
class CheckLocationPermissionUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): LocationPermissionResult {
        return locationRepository.requestLocationPermission()
    }
}
