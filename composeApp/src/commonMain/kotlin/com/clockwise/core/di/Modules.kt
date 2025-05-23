package com.clockwise.core.di

import com.clockwise.company.data.network.KtorRemoteCompanyDataSource
import com.clockwise.company.data.network.RemoteCompanyDataSource
import com.clockwise.company.presentation.CompanyViewModel
import com.clockwise.user.data.network.KtorRemoteUserDataSource
import com.clockwise.user.data.network.RemoteUserDataSource
import com.clockwise.user.data.repository.SearchRepositoryImpl
import com.clockwise.user.domain.repository.SearchRepository
import com.clockwise.user.presentation.home.HomeScreen
import com.clockwise.user.presentation.home.HomeViewModel
import com.clockwise.user.presentation.home.search.SearchViewModel
import com.plcoding.bookpedia.core.data.HttpClientFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.clockwise.user.presentation.user_auth.AuthViewModel
import org.koin.core.module.dsl.viewModel

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get()) }
    viewModel { AuthViewModel(get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get()) }
    viewModel {CompanyViewModel(get())}
    single<SearchRepository> { SearchRepositoryImpl(get()) }
    viewModel {SearchViewModel(get())}
    viewModel {HomeViewModel(get())}
}