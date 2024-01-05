package com.example.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProviderCreateDto(
    @SerialName("name") val name: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("country") val country: String? = null,
    @SerialName("governorate") val governorate: String? = null,
    @SerialName("address") val address: String? = null,
)