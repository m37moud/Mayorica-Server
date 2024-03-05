package com.example.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductUserClient(
    val id: Int = -1,
    val typeCategoryId: Int,
    val sizeCategoryId: Int,
    val colorCategoryId: Int,
    val productName: String,
    val image: String,
    val isHot: Boolean = false,
)
