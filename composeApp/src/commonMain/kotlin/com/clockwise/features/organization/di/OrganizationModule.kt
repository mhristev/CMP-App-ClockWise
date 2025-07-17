package com.clockwise.features.organization.di

import com.clockwise.features.organization.domain.repository.OrganizationRepository
import com.clockwise.features.organization.data.repository.OrganizationRepositoryImpl
import org.koin.dsl.module

val organizationModule = module {
    single<OrganizationRepository> { 
        OrganizationRepositoryImpl(get(), get()) 
    }
}
