package com.clockwise.core.di

import com.clockwise.core.data.KVaultSecureStorage
import com.clockwise.core.data.SecureStorage
import com.clockwise.features.availability.data.network.RemoteAvailabilityDataSource
import com.clockwise.features.availability.data.repository.AvailabilityRepository
import com.clockwise.features.availability.domain.network.KtorRemoteAvailabilityDataSource
import com.clockwise.features.availability.domain.repository.AvailabilityRepositoryImpl
import com.clockwise.features.auth.UserService
import com.clockwise.features.auth.data.network.KtorRemoteUserDataSource
import com.clockwise.features.auth.data.network.RemoteUserDataSource
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
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
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

    // Provide core UserService with SecureStorage
    single {
        UserService(get())
    }

    single { HttpClientFactory.create(get()) }
    single<RemoteUserDataSource> { KtorRemoteUserDataSource(get(), get()) }
    single<RemoteShiftDataSource> { KtorRemoteShiftDataSource(get(), get(), get()) }
    single<ShiftRepository> { ShiftRepositoryImpl(get(), get(), get()) }
    single<RemoteAvailabilityDataSource> { KtorRemoteAvailabilityDataSource(get(), get(), get()) }
    single<AvailabilityRepository> { AvailabilityRepositoryImpl(get()) }
    single<RemoteCompanyDataSource> { KtorRemoteCompanyDataSource(get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get()) }
    single<RemoteUserProfileDataSource> { KtorRemoteUserProfileDataSource(get(), get(), get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }

    single<WorkSessionRepository> { WorkSessionRepositoryImpl(get()) }
    single<RemoteWorkSessionDataSource> {
        RemoteWorkSessionDataSourceImpl(get(), get())
    }

    // Add view models from viewModelModule
    includes(viewModelModule)

    // Auth view model is still defined here since it's not part of our refactoring
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { CompanyViewModel(get()) }
}