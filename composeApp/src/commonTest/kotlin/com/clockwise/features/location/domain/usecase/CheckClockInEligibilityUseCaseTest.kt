package com.clockwise.features.location.domain.usecase

import com.clockwise.core.model.User
import com.clockwise.core.model.UserRole
import com.clockwise.features.location.domain.model.BusinessUnitAddress
import com.clockwise.features.location.domain.model.ClockInEligibility
import com.clockwise.features.location.domain.model.Location
import com.clockwise.features.location.domain.model.LocationResult
import com.clockwise.features.location.domain.repository.LocationRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Example test for the CheckClockInEligibilityUseCase.
 * This demonstrates how to test the location-based clock-in logic.
 */
class CheckClockInEligibilityUseCaseTest {
    
    @Test
    fun `should return eligible when user is within workplace radius`() = runTest {
        // Arrange
        val mockLocationRepository = object : LocationRepository {
            override suspend fun requestLocationPermission() = TODO()
            override suspend fun hasLocationPermission() = true
            override suspend fun getCurrentLocation() = LocationResult.Success(
                Location(
                    latitude = 37.7749,
                    longitude = -122.4194,
                    accuracy = 5.0f
                )
            )
            override fun trackLocationUpdates() = TODO()
            override suspend fun stopLocationTracking() = TODO()
            override fun calculateDistance(location1: Location, location2: Location) = 50.0 // 50 meters
            override fun isWithinRadius(userLocation: Location, targetLocation: Location, allowedRadiusMeters: Double) = true
        }
        
        val useCase = CheckClockInEligibilityUseCase(mockLocationRepository)
        
        val user = User(
            id = "test-user",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            phoneNumber = null,
            role = UserRole.EMPLOYEE,
            businessUnitId = "test-business-unit",
            businessUnitName = "Test Office"
        )
        
        val businessUnitAddress = BusinessUnitAddress(
            street = "123 Test St",
            city = "Test City",
            state = "TS",
            zipCode = "12345",
            country = "Test Country",
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        // Act
        val result = useCase.execute(user, businessUnitAddress)
        
        // Assert
        assertTrue(result is ClockInEligibility.Eligible)
    }
    
    @Test
    fun `should return too far when user is outside workplace radius`() = runTest {
        // Arrange
        val mockLocationRepository = object : LocationRepository {
            override suspend fun requestLocationPermission() = TODO()
            override suspend fun hasLocationPermission() = true
            override suspend fun getCurrentLocation() = LocationResult.Success(
                Location(
                    latitude = 37.8749, // Different location
                    longitude = -122.5194,
                    accuracy = 5.0f
                )
            )
            override fun trackLocationUpdates() = TODO()
            override suspend fun stopLocationTracking() = TODO()
            override fun calculateDistance(location1: Location, location2: Location) = 500.0 // 500 meters
            override fun isWithinRadius(userLocation: Location, targetLocation: Location, allowedRadiusMeters: Double) = false
        }
        
        val useCase = CheckClockInEligibilityUseCase(mockLocationRepository)
        
        val user = User(
            id = "test-user",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            phoneNumber = null,
            role = UserRole.EMPLOYEE,
            businessUnitId = "test-business-unit",
            businessUnitName = "Test Office"
        )
        
        val businessUnitAddress = BusinessUnitAddress(
            street = "123 Test St",
            city = "Test City",
            state = "TS",
            zipCode = "12345",
            country = "Test Country",
            latitude = 37.7749,
            longitude = -122.4194
        )
        
        // Act
        val result = useCase.execute(user, businessUnitAddress)
        
        // Assert
        assertTrue(result is ClockInEligibility.TooFarFromWorkplace)
        val tooFarResult = result as ClockInEligibility.TooFarFromWorkplace
        assertTrue(tooFarResult.distanceInMeters == 500.0)
        assertTrue(tooFarResult.allowedRadiusInMeters == 100.0)
    }
    
    @Test
    fun `should return no business unit when user has no business unit assigned`() = runTest {
        // Arrange
        val mockLocationRepository = object : LocationRepository {
            override suspend fun requestLocationPermission() = TODO()
            override suspend fun hasLocationPermission() = true
            override suspend fun getCurrentLocation() = TODO()
            override fun trackLocationUpdates() = TODO()
            override suspend fun stopLocationTracking() = TODO()
            override fun calculateDistance(location1: Location, location2: Location) = TODO()
            override fun isWithinRadius(userLocation: Location, targetLocation: Location, allowedRadiusMeters: Double) = TODO()
        }
        
        val useCase = CheckClockInEligibilityUseCase(mockLocationRepository)
        
        val user = User(
            id = "test-user",
            email = "test@example.com",
            firstName = "Test",
            lastName = "User",
            phoneNumber = null,
            role = UserRole.EMPLOYEE,
            businessUnitId = null, // No business unit
            businessUnitName = null
        )
        
        // Act
        val result = useCase.execute(user, null)
        
        // Assert
        assertTrue(result is ClockInEligibility.NoBusinessUnitAssigned)
    }
}
