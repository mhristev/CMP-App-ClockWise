package com.clockwise.core.di

import com.clockwise.features.company.data.network.KtorRemoteCompanyDataSource
import com.clockwise.features.company.data.network.RemoteCompanyDataSource
import com.clockwise.features.company.presentation.CompanyViewModel
import com.clockwise.features.availability.calendar.domain.AvailabilityRepository
import com.clockwise.features.shift.data.repository.ShiftRepository
import com.clockwise.features.shift.domain.repositories.ShiftRepositoryImpl
import com.clockwise.features.shift.domain.network.KtorRemoteShiftDataSource
import com.clockwise.features.shift.data.network.RemoteShiftDataSource
import com.clockwise.core.UserService
import com.clockwise.features.auth.data.network.KtorRemoteUserDataSource
import com.clockwise.features.auth.data.network.RemoteUserDataSource
import com.clockwise.features.business.domain.repository.SearchRepositoryImpl
import com.clockwise.features.business.data.repository.SearchRepository
import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import com.clockwise.features.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import com.clockwise.di.viewModelModule
import com.clockwise.features.profile.data.repository.ProfileRepository
import com.clockwise.features.profile.domain.repository.ProfileRepositoryImpl

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get(), get()) }
    single { UserService() }
    single<RemoteShiftDataSource> { KtorRemoteShiftDataSource(get(), get(), get()) }
    single<ShiftRepository> { ShiftRepositoryImpl(get()) }
    single { AvailabilityRepository(get(), get(), get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get(), get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get()) }

    // Add view models from viewModelModule
    includes(viewModelModule)
    
    // Auth view model is still defined here since it's not part of our refactoring
    viewModel { AuthViewModel(get(), get()) }
    viewModel { CompanyViewModel(get()) }
}