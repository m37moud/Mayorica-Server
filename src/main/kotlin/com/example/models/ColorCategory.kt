package com.example.models

data class ColorCategory(
    val id: Int = -1,
    val typeCategoryId: Int,
    val sizeCategoryId: Int,
    val color: String,
    val userAdminID: Int,
    val createdAt: String,
    val updatedAt: String
)

