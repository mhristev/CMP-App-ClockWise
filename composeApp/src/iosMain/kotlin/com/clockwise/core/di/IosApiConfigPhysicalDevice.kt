package com.clockwise.core.di

/**
 * iOS API configuration for physical device testing.
 * Use this configuration when testing on a physical iOS device.
 * Replace IosApiConfig() with IosApiConfigPhysicalDevice() in PlatformModule.kt
 */
class IosApiConfigPhysicalDevice : ApiConfig {
    // Use your Mac's IP address for physical device testing
    private val baseHost = "http://192.168.100.185"
    
    override val baseAuthUrl: String = "$baseHost:8081/v1/auth"
    override val baseUsersUrl: String = "$baseHost:8082/v1/users"
    override val baseCompaniesUrl: String = "$baseHost:8084/v1/companies"
    override val baseAvailabilityUrl: String = "$baseHost:8083/v1"
    override val baseShiftUrl: String = "$baseHost:8083/v1"
    override val gdprUrl: String = "$baseHost:8082/v1/gdpr"
    override val baseWorkSessionUrl: String = "$baseHost:8083/v1/work-sessions"
    override val baseOrganizationUrl: String = "$baseHost:8084/v1"
}
