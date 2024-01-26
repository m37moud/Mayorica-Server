package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ColorCategoryMenu(
    val colorId: Int,
    val colorName: String,
    val colorValue: String,
)
