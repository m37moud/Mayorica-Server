package com.example.models.dto

data class CeramicCreateDto(
    val typeCategoryId: Int,
    val sizeCategoryId: Int,
    val colorCategoryId: Int,
    val productName: String,
    val productImageUrl: String,
)
