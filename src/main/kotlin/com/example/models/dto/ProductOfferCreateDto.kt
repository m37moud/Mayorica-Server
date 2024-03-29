package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductOfferCreateDto(
    val offerTitle: String,
    val offerDescription: String,
    val offerImageUrl: String?,
    val offerEndDate: String,
)
