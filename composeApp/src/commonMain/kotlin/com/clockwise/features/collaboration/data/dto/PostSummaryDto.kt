package com.clockwise.features.collaboration.data.dto

import com.clockwise.features.collaboration.domain.model.Post
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PostSummaryDto(
    val id: String,
    val title: String,
    val authorUserId: String,
    val businessUnitId: String,
    val targetAudience: String,
    val creatorUserFirstName: String? = null,
    val creatorUserLastName: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    fun toDomainModel(): Post {
        return Post(
            id = id,
            title = title,
            body = "", // Empty body for summary view
            authorUserId = authorUserId,
            businessUnitId = businessUnitId,
            targetAudience = mapTargetAudience(targetAudience),
            creatorUserFirstName = creatorUserFirstName,
            creatorUserLastName = creatorUserLastName,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun mapTargetAudience(audience: String): Post.TargetAudience {
        return when (audience.uppercase()) {
            "ALL_EMPLOYEES" -> Post.TargetAudience.ALL_EMPLOYEES
            "MANAGERS_ONLY" -> Post.TargetAudience.MANAGERS_ONLY
            "DEPARTMENT_ONLY" -> Post.TargetAudience.DEPARTMENT_ONLY
            "TEAM_SPECIFIC" -> Post.TargetAudience.TEAM_SPECIFIC
            else -> Post.TargetAudience.ALL_EMPLOYEES
        }
    }
}
