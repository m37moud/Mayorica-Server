package com.example.models.dto

data class ProductOfferCreateDto(
    val offerTitle: String,
    val offerDescription: String,
    val offerImageUrl: String?,
    val offerEndDate: String,
)
