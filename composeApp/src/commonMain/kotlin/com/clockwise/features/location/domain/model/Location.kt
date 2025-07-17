package com.clockwise.features.location.domain.model

import kotlinx.datetime.Clock

data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Double? = null,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)