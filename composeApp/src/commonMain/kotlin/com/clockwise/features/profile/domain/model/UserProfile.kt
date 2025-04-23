package com.clockwise.features.profile.domain.model

/**
 * Domain model representing user profile information
 */
data class UserProfile(
    val name: String,
    val email: String,
    val role: String,
    val company: String,
    val phone: String? = null
) 