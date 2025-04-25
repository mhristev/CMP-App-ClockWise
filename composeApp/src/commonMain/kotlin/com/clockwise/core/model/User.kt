package com.clockwise.core.model

import kotlinx.serialization.Serializable

enum class UserRole {
    ADMIN,
    MANAGER,
    EMPLOYEE
}

@Serializable
data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val role: UserRole,
    val businessUnitId: String?,
    val businessUnitName: String?,
    val hasProvidedConsent: Boolean = false
)

@Serializable
data class PrivacyConsent(
    val marketingConsent: Boolean = false,
    val analyticsConsent: Boolean = false,
    val thirdPartyDataSharingConsent: Boolean = false
)
