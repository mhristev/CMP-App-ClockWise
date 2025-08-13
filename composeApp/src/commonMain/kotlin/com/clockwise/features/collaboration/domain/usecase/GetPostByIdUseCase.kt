package com.clockwise.features.collaboration.domain.usecase

import com.clockwise.features.collaboration.domain.model.Post
import com.clockwise.features.collaboration.domain.repository.CollaborationRepository

class GetPostByIdUseCase(
    private val collaborationRepository: CollaborationRepository
) {
    suspend operator fun invoke(postId: String): Post? {
        return collaborationRepository.getPostById(postId)
    }
}