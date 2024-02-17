package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class OfferDto(
    val id: Int = -1,
    val adminUserName: String,
    val title: String,
    val offerDescription: String,
    val image: String? = null,
    val isHotOffer: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val endedAt: String,

    )
