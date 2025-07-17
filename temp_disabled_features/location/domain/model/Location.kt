package com.clockwise.features.location.domain.model

data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
