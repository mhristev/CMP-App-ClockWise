package com.clockwise.features.availability.data.network

import com.clockwise.features.availability.data.dto.AvailabilityDto
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.datetime.LocalDate

interface RemoteAvailabilityDataSource {
    suspend fun getUserAvailabilities(): Result<List<AvailabilityDto>, DataError.Remote>
    
    suspend fun createAvailability(
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Result<AvailabilityDto, DataError.Remote>
    
    suspend fun updateAvailability(
        id: String,
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Result<AvailabilityDto, DataError.Remote>
    
    suspend fun deleteAvailability(id: String): Result<Boolean, DataError.Remote>
} 