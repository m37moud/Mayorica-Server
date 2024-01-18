package com.example.models.dto

data class SizeCategoryDto(
    val id: Int = -1,
    val adminUserName: String = "",
    val size: String,
    val sizeImage: String="",
    val userAdminID: Int,
    val createdAt: String="",
    val updatedAt: String=""
)
