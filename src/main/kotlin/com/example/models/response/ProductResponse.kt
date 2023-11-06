package com.example.models.response

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: Int = -1,
    val typeCategoryName: String,
    val sizeCategoryName: String,
    val colorCategoryName: String,
    val productName: String,
    val image: String,
    val createdAt: String,
    val updatedAt: String,
) {
}