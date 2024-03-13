package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class SizeCategoryUserClientResponse(
    val id: Int = -1,
    val typeCategoryId: Int,
    val size: String,
    val sizeImage: String="",
)
