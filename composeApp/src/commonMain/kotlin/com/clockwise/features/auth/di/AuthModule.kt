package com.clockwise.features.auth.di

import com.clockwise.features.auth.data.network.KtorRemoteUserDataSource
import com.clockwise.features.auth.data.network.RemoteUserDataSource
import com.clockwise.features.auth.data.repository.AuthRepositoryImpl
import com.clockwise.features.auth.domain.repository.AuthRepository
import com.clockwise.features.auth.presentation.AuthViewModel
import com.clockwise.features.auth.presentation.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * DI module for the auth feature
 */
val authModule: Module = module {
    // Data sources
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get(), get()) }
    
    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    
    // ViewModels
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get(), get()) }
}