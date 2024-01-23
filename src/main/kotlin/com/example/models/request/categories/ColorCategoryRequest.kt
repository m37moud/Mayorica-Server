package com.example.models.request.categories

import kotlinx.serialization.Serializable

@Serializable
data class ColorCategoryRequest(
    val colorName: String,
    val colorValue: String,

    )
