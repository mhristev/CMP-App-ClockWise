package com.clockwise.features.shift.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val timestamp: List<Int>? = null,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
) 