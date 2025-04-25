package com.clockwise.core.di

class IosApiConfig : ApiConfig {
    override val baseAuthUrl: String = "http://localhost:8081/v1/auth"
    override val baseUsersUrl: String = "http://localhost:8081/v1/users"
    override val baseCompaniesUrl: String = "http://localhost:8080/v1/companies"
    override val baseAvailabilityUrl: String = "http://localhost:8080/v1"
    override val baseShiftUrl: String = "http://localhost:8080/v1"
    override val gdprUrl: String = "http://localhost:8081/v1/gdpr"
} 