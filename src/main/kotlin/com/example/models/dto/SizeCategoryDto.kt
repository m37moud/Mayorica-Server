package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class SizeCategoryDto(
    val id: Int = -1,
    val adminUserName: String,
    val typeCategoryName: String,
    val size: String,
    val sizeImage: String,
    val createdAt: String,
    val updatedAt: String,
)
