package com.clockwise.features.managerapproval.di

import com.clockwise.features.managerapproval.data.network.KtorRemoteManagerApprovalDataSource
import com.clockwise.features.managerapproval.data.network.RemoteManagerApprovalDataSource
import com.clockwise.features.managerapproval.data.repository.ManagerApprovalRepositoryImpl
import com.clockwise.features.managerapproval.domain.repository.ManagerApprovalRepository
import com.clockwise.features.managerapproval.domain.usecase.ApproveExchangeUseCase
import com.clockwise.features.managerapproval.domain.usecase.GetPendingExchangesUseCase
import com.clockwise.features.managerapproval.domain.usecase.RejectExchangeUseCase
import com.clockwise.features.managerapproval.presentation.ManagerApprovalViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val managerApprovalModule = module {
    
    // Data sources
    single<RemoteManagerApprovalDataSource> { 
        KtorRemoteManagerApprovalDataSource(get(), get(), get()) 
    }
    
    // Repositories
    single<ManagerApprovalRepository> { 
        ManagerApprovalRepositoryImpl(get()) 
    }
    
    // Use cases
    factory { GetPendingExchangesUseCase(get()) }
    factory { ApproveExchangeUseCase(get()) }
    factory { RejectExchangeUseCase(get()) }
    
    // ViewModels
    viewModel { ManagerApprovalViewModel(get(), get(), get(), get()) }
}