package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorCategory(
    val id: Int = -1,
    @SerialName("color") val colorName: String,
    val colorValue: String,
    val userAdminID: Int,
    val createdAt: String,
    val updatedAt: String
)

/**
 * ColorCategory DTO for response with pagination
 */
@Serializable
data class ColorCategoryPage(
    val page: Int,
    val perPage: Int,
    val data: List<ColorCategory>
)


