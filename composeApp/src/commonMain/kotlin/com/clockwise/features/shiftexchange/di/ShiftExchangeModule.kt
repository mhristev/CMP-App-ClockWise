package com.clockwise.features.shiftexchange.di

import com.clockwise.features.shiftexchange.data.network.KtorRemoteShiftExchangeDataSource
import com.clockwise.features.shiftexchange.data.network.RemoteShiftExchangeDataSource
import com.clockwise.features.shiftexchange.data.repository.ShiftExchangeRepositoryImpl
import com.clockwise.features.shiftexchange.domain.repository.ShiftExchangeRepository
import com.clockwise.features.shiftexchange.domain.usecase.AcceptRequestUseCase
import com.clockwise.features.shiftexchange.domain.usecase.GetAvailableShiftsUseCase
import com.clockwise.features.shiftexchange.domain.usecase.GetMyPostedShiftsUseCase
import com.clockwise.features.shiftexchange.domain.usecase.GetRequestsForMyShiftUseCase
import com.clockwise.features.shiftexchange.domain.usecase.PostShiftToMarketplaceUseCase
import com.clockwise.features.shiftexchange.domain.usecase.SubmitShiftRequestUseCase
import com.clockwise.features.shiftexchange.presentation.ShiftExchangeViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val shiftExchangeModule = module {
    
    // Data Sources
    single<RemoteShiftExchangeDataSource> {
        KtorRemoteShiftExchangeDataSource(
            httpClient = get(),
            apiConfig = get()
        )
    }
    
    // Repositories
    single<ShiftExchangeRepository> {
        ShiftExchangeRepositoryImpl(
            remoteDataSource = get()
        )
    }
    
    // Use Cases
    single {
        GetAvailableShiftsUseCase(
            repository = get()
        )
    }
    
    single {
        GetMyPostedShiftsUseCase(
            repository = get()
        )
    }
    
    single {
        PostShiftToMarketplaceUseCase(
            repository = get()
        )
    }
    
    single {
        SubmitShiftRequestUseCase(
            repository = get()
        )
    }
    
    single {
        GetRequestsForMyShiftUseCase(
            repository = get()
        )
    }
    
    single {
        AcceptRequestUseCase(
            repository = get()
        )
    }
    
    // ViewModels
    viewModel {
        ShiftExchangeViewModel(
            getAvailableShiftsUseCase = get(),
            getMyPostedShiftsUseCase = get(),
            postShiftToMarketplaceUseCase = get(),
            submitShiftRequestUseCase = get(),
            getRequestsForMyShiftUseCase = get(),
            acceptRequestUseCase = get(),
            shiftRepository = get(),
            userService = get()
        )
    }
}