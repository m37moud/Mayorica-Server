package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class CeramicProviderResponse(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: Strig,
    val governorate: String,
    val address: String
)
