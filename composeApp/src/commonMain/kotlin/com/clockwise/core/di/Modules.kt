package com.clockwise.core.di

import com.clockwise.core.data.KVaultSecureStorage
import com.clockwise.core.data.SecureStorage
import com.clockwise.core.data.DataClearingService
import com.clockwise.core.data.DefaultDataClearingService
import com.clockwise.core.data.PlatformDataCleaner
import com.clockwise.features.availability.data.network.RemoteAvailabilityDataSource
import com.clockwise.features.availability.data.repository.AvailabilityRepository
import com.clockwise.features.availability.domain.network.KtorRemoteAvailabilityDataSource
import com.clockwise.features.availability.domain.repository.AvailabilityRepositoryImpl
import com.clockwise.features.auth.UserService
import com.clockwise.features.auth.presentation.AuthViewModel
import com.clockwise.features.business.data.repository.UserRepository
import com.clockwise.features.business.domain.repository.UserRepositoryImpl
import com.clockwise.features.company.data.network.KtorRemoteCompanyDataSource
import com.clockwise.features.company.data.network.RemoteCompanyDataSource
import com.clockwise.features.company.presentation.CompanyViewModel
import com.clockwise.features.profile.data.network.KtorRemoteUserProfileDataSource
import com.clockwise.features.profile.data.network.RemoteUserProfileDataSource
import com.clockwise.features.profile.data.repository.ProfileRepository
import com.clockwise.features.profile.data.repository.UserProfileRepositoryImpl
import com.clockwise.features.profile.domain.repository.ProfileRepositoryImpl
import com.clockwise.features.profile.domain.repository.UserProfileRepository
import com.clockwise.features.shift.data.network.RemoteShiftDataSource
import com.clockwise.features.shift.data.network.RemoteWorkSessionDataSource
import com.clockwise.features.shift.data.network.RemoteWorkSessionDataSourceImpl
import com.clockwise.features.shift.data.repository.WorkSessionRepository
import com.clockwise.features.shift.data.repository.WorkSessionRepositoryImpl
import com.clockwise.features.shift.domain.network.KtorRemoteShiftDataSource
import com.clockwise.features.shift.domain.repositories.ShiftRepository
import com.clockwise.features.shift.domain.repositories.ShiftRepositoryImpl
import com.plcoding.bookpedia.core.data.HttpClientFactory
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


// For KVault we need platform-specific initialization
expect val platformModule: Module

val sharedModule = module {
    // Provide Json serializer
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true          // Allow flexible parsing of JSON
            coerceInputValues = true  // Handle null values more gracefully
            prettyPrint = false
            explicitNulls = false     // Make serialization more forgiving
        }
    }

    // KVault instance is now provided by platformModule

    // Provide SecureStorage implementation
    single<SecureStorage> {
        KVaultSecureStorage(get(), get())
    }

    // Provide DataClearingService implementation
    single<DataClearingService> {
        DefaultDataClearingService(get(), get())
    }

    // Provide core UserService with DataClearingService
    single {
        UserService(get(), get())
    }

    // Note: LocationService is provided by platformModule (Android/iOS specific implementations)

    // Provide authenticated HttpClient for API calls that require Bearer tokens
    single<HttpClient>(qualifier = named("authenticated")) { 
        HttpClientFactory.createAuthenticated(get(), get()) 
    }
    
    // Provide public HttpClient for auth endpoints that should NOT send Bearer tokens
    single<HttpClient>(qualifier = named("public")) { 
        HttpClientFactory.createPublic(get()) 
    }
    
    // Legacy HttpClient for backward compatibility - defaults to authenticated
    single<HttpClient> { 
        HttpClientFactory.createAuthenticated(get(), get()) 
    }
    // Note: RemoteUserDataSource is provided by authModule to avoid duplication
    single<RemoteShiftDataSource> { KtorRemoteShiftDataSource(get(), get(), get()) }
    single<ShiftRepository> { ShiftRepositoryImpl(get(), get(), get()) }
    single<RemoteAvailabilityDataSource> { KtorRemoteAvailabilityDataSource(get(), get(), get()) }
    single<AvailabilityRepository> { AvailabilityRepositoryImpl(get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get(), get()) }
    single<RemoteUserProfileDataSource> { KtorRemoteUserProfileDataSource(get(), get(), get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }

    single<WorkSessionRepository> { WorkSessionRepositoryImpl(get()) }
    single<RemoteWorkSessionDataSource> {
        RemoteWorkSessionDataSourceImpl(get(), get(), get())
    }

    // Add view models from viewModelModule
    includes(viewModelModule)

    // Auth view model is still defined here since it's not part of our refactoring
    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { CompanyViewModel(get()) }
}