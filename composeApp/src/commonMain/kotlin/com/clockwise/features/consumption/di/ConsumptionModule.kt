package com.clockwise.features.consumption.di

import com.clockwise.features.consumption.data.network.KtorRemoteConsumptionDataSource
import com.clockwise.features.consumption.data.network.RemoteConsumptionDataSource
import com.clockwise.features.consumption.data.repository.ConsumptionRepositoryImpl
import com.clockwise.features.consumption.domain.repository.ConsumptionRepository
import org.koin.dsl.module

val consumptionModule = module {
    single<RemoteConsumptionDataSource> {
        KtorRemoteConsumptionDataSource(get(), get())
    }
    
    single<ConsumptionRepository> {
        ConsumptionRepositoryImpl(get())
    }
}