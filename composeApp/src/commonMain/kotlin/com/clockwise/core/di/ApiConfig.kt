package com.clockwise.core.di

interface ApiConfig {
    val baseAuthUrl: String
    val baseUsersUrl: String
    val baseCompaniesUrl: String
    val baseAvailabilityUrl: String
    val baseShiftUrl: String
    val gdprUrl: String
} 