package com.clockwise.features.availability.data.repository

import com.clockwise.features.availability.data.dto.AvailabilityDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Repository interface for availability-related operations
 */
interface AvailabilityRepository {
    /**
     * Get all availabilities for the current user
     */
    suspend fun getUserAvailabilities(): Flow<Result<List<AvailabilityDto>, DataError.Remote>>
    
    /**
     * Create a new availability
     */
    suspend fun createAvailability(
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Flow<Result<AvailabilityDto, DataError.Remote>>
    
    /**
     * Update an existing availability
     */
    suspend fun updateAvailability(
        id: String,
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Flow<Result<AvailabilityDto, DataError.Remote>>
    
    /**
     * Delete an availability
     */
    suspend fun deleteAvailability(id: String): Flow<Result<Boolean, DataError.Remote>>
} 