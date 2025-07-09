package com.clockwise.core.di

class IosApiConfig : ApiConfig {
    override val baseAuthUrl: String = "http://127.0.0.1:8081/v1/auth"
    override val baseUsersUrl: String = "http://127.0.0.1:8082/v1/users"
    override val baseCompaniesUrl: String = "http://127.0.0.1:8084/v1/companies"
    override val baseAvailabilityUrl: String = "http://127.0.0.1:8083/v1"
    override val baseShiftUrl: String = "http://127.0.0.1:8083/v1"
    override val gdprUrl: String = "http://127.0.0.1:8082/v1/gdpr"
    override val baseWorkSessionUrl: String = "http://127.0.0.1:8083/v1/work-sessions"
} 