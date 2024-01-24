package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    val id: Int = -1,
    val adminUserName: String,
    val typeCategoryName: String,
    val sizeCategoryName: String,
    val colorCategoryName: String,
    val productName: String,
    val image: String,
    val createdAt: String,
    val updatedAt: String,
    val deleted: Boolean = false

)
