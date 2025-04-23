package com.clockwise.features.availability.domain.repository

import com.clockwise.features.availability.data.dto.AvailabilityDto
import com.clockwise.features.availability.data.network.RemoteAvailabilityDataSource
import com.clockwise.features.availability.data.repository.AvailabilityRepository
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate

/**
 * Implementation of the AvailabilityRepository that uses RemoteAvailabilityDataSource
 */
class AvailabilityRepositoryImpl(
    private val remoteDataSource: RemoteAvailabilityDataSource
) : AvailabilityRepository {
    
    override suspend fun getUserAvailabilities(): Flow<Result<List<AvailabilityDto>, DataError.Remote>> {
        return flow {
            val result = remoteDataSource.getUserAvailabilities()
            emit(result)
        }
    }
    
    override suspend fun createAvailability(
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Flow<Result<AvailabilityDto, DataError.Remote>> {
        return flow {
            val result = remoteDataSource.createAvailability(date, startTimeString, endTimeString)
            emit(result)
        }
    }
    
    override suspend fun updateAvailability(
        id: String,
        date: LocalDate, 
        startTimeString: String, 
        endTimeString: String
    ): Flow<Result<AvailabilityDto, DataError.Remote>> {
        return flow {
            val result = remoteDataSource.updateAvailability(id, date, startTimeString, endTimeString)
            emit(result)
        }
    }
    
    override suspend fun deleteAvailability(id: String): Flow<Result<Boolean, DataError.Remote>> {
        return flow {
            val result = remoteDataSource.deleteAvailability(id)
            emit(result)
        }
    }
} 