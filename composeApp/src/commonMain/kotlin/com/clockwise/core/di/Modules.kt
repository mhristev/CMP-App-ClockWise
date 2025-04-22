package com.clockwise.core.di

import com.clockwise.features.company.data.network.KtorRemoteCompanyDataSource
import com.clockwise.features.company.data.network.RemoteCompanyDataSource
import com.clockwise.features.company.presentation.CompanyViewModel
import com.clockwise.features.availability.calendar.domain.AvailabilityRepository
import com.clockwise.features.shift.schedule.domain.ShiftRepository
import com.clockwise.core.UserService
import com.clockwise.features.auth.data.network.KtorRemoteUserDataSource
import com.clockwise.features.auth.data.network.RemoteUserDataSource
import com.clockwise.features.business.domain.repository.SearchRepositoryImpl
import com.clockwise.features.business.data.repository.SearchRepository
import com.clockwise.features.welcome.presentation.HomeViewModel
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import com.clockwise.features.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.di.viewModelModule

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get(), get()) }
    single { UserService() }
    single { ShiftRepository(get(), get()) }
    single { AvailabilityRepository(get(), get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get(), get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    
    // Add view models from viewModelModule
    includes(viewModelModule)
    
    // Auth view model is still defined here since it's not part of our refactoring
    viewModel { AuthViewModel(get(), get()) }
    viewModel { CompanyViewModel(get()) }
}