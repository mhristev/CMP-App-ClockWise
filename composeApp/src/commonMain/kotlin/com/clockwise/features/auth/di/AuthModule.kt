package com.clockwise.features.auth.di

import com.clockwise.features.auth.data.network.KtorRemoteUserDataSource
import com.clockwise.features.auth.data.network.RemoteUserDataSource
import com.clockwise.features.auth.data.repository.AuthRepositoryImpl
import com.clockwise.features.auth.domain.repository.AuthRepository
import com.clockwise.features.auth.presentation.AuthViewModel
import com.clockwise.features.auth.presentation.SplashViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * DI module for the auth feature
 */
val authModule: Module = module {
    // Data sources - use public HttpClient for auth endpoints (no Bearer tokens)
    single<RemoteUserDataSource> { 
        KtorRemoteUserDataSource(
            publicHttpClient = get(qualifier = named("public")), 
            apiConfig = get()
        ) 
    }
    
    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    
    // ViewModels
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { SplashViewModel(get(), get()) }
}