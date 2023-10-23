package com.example.models

data class SizeCategory(
    val id: Int = -1,
    val typeCategoryId: Int,
    val size: String,
    val userAdminID: Int,
    val createdAt: String,
    val updatedAt: String
)
