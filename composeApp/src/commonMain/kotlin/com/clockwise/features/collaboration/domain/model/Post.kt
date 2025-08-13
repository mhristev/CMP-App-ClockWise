package com.clockwise.features.collaboration.domain.model

import kotlinx.datetime.Instant

data class Post(
    val id: String,
    val title: String,
    val body: String,
    val authorUserId: String,
    val businessUnitId: String,
    val targetAudience: TargetAudience,
    val creatorUserFirstName: String? = null,
    val creatorUserLastName: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    enum class TargetAudience {
        ALL_EMPLOYEES,
        MANAGERS_ONLY,
        DEPARTMENT_ONLY,
        TEAM_SPECIFIC
    }
    
    val authorFullName: String
        get() = buildString {
            creatorUserFirstName?.let { append(it) }
            if (!creatorUserFirstName.isNullOrBlank() && !creatorUserLastName.isNullOrBlank()) {
                append(" ")
            }
            creatorUserLastName?.let { append(it) }
        }.ifBlank { "Unknown Author" }
}