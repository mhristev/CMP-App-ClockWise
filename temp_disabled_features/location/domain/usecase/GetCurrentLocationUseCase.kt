package com.clockwise.features.location.domain.usecase

import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.repository.LocationRepository

/**
 * Use case for getting the current location
 */
class GetCurrentLocationUseCase(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): LocationResult {
        return locationRepository.getCurrentLocation()
    }
}
