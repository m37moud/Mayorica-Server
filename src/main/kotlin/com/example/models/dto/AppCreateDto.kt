package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class AppCreateDto(
    val packageName: String,
    val currentVersion: Double,
)
