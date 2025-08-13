package com.clockwise.features.collaboration.domain.usecase

import com.clockwise.features.collaboration.domain.model.Post
import com.clockwise.features.collaboration.domain.repository.CollaborationRepository
import com.clockwise.features.auth.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetPostsUseCase(
    private val collaborationRepository: CollaborationRepository,
    private val userService: UserService
) {
    suspend operator fun invoke(
        page: Int = 0,
        size: Int = 20
    ): Flow<List<Post>> {
        val user = userService.currentUser.first()
            ?: throw IllegalStateException("User not authenticated")
        
        val businessUnitId = user.businessUnitId
            ?: throw IllegalStateException("User does not have a business unit")
        
        return collaborationRepository.getPostsForBusinessUnit(
            businessUnitId = businessUnitId,
            page = page,
            size = size
        )
    }
}