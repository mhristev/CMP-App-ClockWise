package com.clockwise.core.di

class IosApiConfig : ApiConfig {
    // For iOS Simulator use 127.0.0.1, for physical device use your Mac's IP address
    // Switch between "http://127.0.0.1" and "http://192.168.100.185" as needed
    private val baseHost = "http://192.168.100.185" // Use this for physical device testing
    
    override val baseAuthUrl: String = "$baseHost:8081/v1/auth"
    override val baseUsersUrl: String = "$baseHost:8082/v1/users"
    override val baseCompaniesUrl: String = "$baseHost:8084/v1/companies"
    override val baseAvailabilityUrl: String = "$baseHost:8083/v1"
    override val baseShiftUrl: String = "$baseHost:8083/v1"
    override val gdprUrl: String = "$baseHost:8082/v1/gdpr"
    override val baseWorkSessionUrl: String = "$baseHost:8083/v1/work-sessions"
    override val baseOrganizationUrl: String = "$baseHost:8084/v1"
    override val baseCollaborationUrl: String = "$baseHost:8085/v1"
} 