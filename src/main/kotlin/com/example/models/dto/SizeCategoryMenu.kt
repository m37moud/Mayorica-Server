package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class SizeCategoryMenu(
    val sizeId: Int,
    val sizeName: String
)
