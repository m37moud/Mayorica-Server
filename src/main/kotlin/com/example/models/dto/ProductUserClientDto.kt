package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductUserClientDto(
    val id: Int = -1,
    val typeCategoryName: String,
    val sizeCategoryName: String,
    val colorCategoryName: String,
    val productName: String,
    val image: String,
    val isHot :Boolean = false,
)
