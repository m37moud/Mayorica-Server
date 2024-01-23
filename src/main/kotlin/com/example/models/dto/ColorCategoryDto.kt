package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ColorCategoryDto(
    val id: Int,
    val adminUserName: String,
    val colorName: String,
    val colorValue: String,
    val createdAt: String,
    val updatedAt: String
)
