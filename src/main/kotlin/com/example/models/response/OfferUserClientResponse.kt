package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class OfferUserClientResponse(
    val id: Int = -1,
    val title: String,
    val offerDescription: String,
    val image: String? = null,
    val isHotOffer: Boolean,
    val createdAt: String,
    val endedAt: String,
)
