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

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get(), get()) }
    single { UserService() }
    single { ShiftRepository(get(), get()) }
    single { AvailabilityRepository(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get(), get()) }
    viewModel { CompanyViewModel(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { BusinessViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }
}