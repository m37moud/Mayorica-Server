package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class CeramicProviderResponse(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val city: String,
    val address: String
)
