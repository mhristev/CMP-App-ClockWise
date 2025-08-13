package com.clockwise.features.collaboration.di

import com.clockwise.features.collaboration.data.network.KtorRemoteCollaborationDataSource
import com.clockwise.features.collaboration.data.network.RemoteCollaborationDataSource
import com.clockwise.features.collaboration.data.repository.CollaborationRepositoryImpl
import com.clockwise.features.collaboration.domain.repository.CollaborationRepository
import com.clockwise.features.collaboration.domain.usecase.GetPostByIdUseCase
import com.clockwise.features.collaboration.domain.usecase.GetPostsUseCase
import com.clockwise.features.collaboration.presentation.PostsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val collaborationModule = module {
    // Data layer
    single<RemoteCollaborationDataSource> { 
        KtorRemoteCollaborationDataSource(get(), get()) 
    }
    
    single<CollaborationRepository> { 
        CollaborationRepositoryImpl(get()) 
    }
    
    // Domain layer
    single { GetPostsUseCase(get(), get()) }
    single { GetPostByIdUseCase(get()) }
    
    // Presentation layer
    viewModel { PostsViewModel(get(), get()) }
}