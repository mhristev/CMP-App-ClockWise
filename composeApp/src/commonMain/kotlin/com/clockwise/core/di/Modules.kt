package com.clockwise.core.di

import com.clockwise.company.data.network.KtorRemoteCompanyDataSource
import com.clockwise.company.data.network.RemoteCompanyDataSource
import com.clockwise.company.presentation.CompanyViewModel
import com.clockwise.service.ShiftService
import com.clockwise.service.UserService
import com.clockwise.user.data.network.KtorRemoteUserDataSource
import com.clockwise.user.data.network.RemoteUserDataSource
import com.clockwise.user.data.repository.SearchRepositoryImpl
import com.clockwise.user.domain.repository.SearchRepository
import com.clockwise.user.presentation.home.HomeScreen
import com.clockwise.user.presentation.home.HomeViewModel
import com.clockwise.user.presentation.home.profile.ProfileViewModel
import com.clockwise.user.presentation.home.search.SearchViewModel
import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.clockwise.user.presentation.user_auth.AuthViewModel
import org.koin.core.module.dsl.viewModel
import com.clockwise.user.presentation.home.business.BusinessViewModel

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get(), get()) }
    single { UserService() }
    single { ShiftService(get(), get()) }
    viewModel { AuthViewModel(get(), get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get(), get()) }
    viewModel { CompanyViewModel(get()) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { BusinessViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ProfileViewModel(get()) }
}