package com.example.models

data class Product(
    val id: Int = -1,
    val typeCategoryId: Int,
    val sizeCategoryId: Int,
    val colorCategoryId: Int,
    val productName: String,
    val image: String,
    val createdAt: String,
    val updatedAt: String,
    val deleted: Boolean = false

)
