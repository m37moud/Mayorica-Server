package com.example.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorCategoryUserClientResponse(
    val id: Int = -1,
    @SerialName("color") val colorName: String,
    val colorValue: String,
)
