package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class SizeCategoryCreateDto(
    val typeCategoryId: Int,
    val sizeName: String,
    val sizeImageUrl: String,
)
