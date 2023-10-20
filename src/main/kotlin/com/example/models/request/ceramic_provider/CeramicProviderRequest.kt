package com.example.models.request.ceramic_provider

import kotlinx.serialization.Serializable

@Serializable
data class CeramicProviderRequest(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val governorate: String,
    val address: String
)
