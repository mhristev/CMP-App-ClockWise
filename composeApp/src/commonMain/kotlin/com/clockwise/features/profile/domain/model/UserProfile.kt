package com.clockwise.features.profile.domain.model

/**
 * Domain model representing user profile information
 */
data class UserProfile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val company: String,
    val phoneNumber: String? = null
) 